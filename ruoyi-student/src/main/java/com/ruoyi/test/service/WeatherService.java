package com.ruoyi.test.service;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.utils.ObjectMapperUtil;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.system.service.ISysLogininforService;
import com.ruoyi.test.MsgProducer;
import com.ruoyi.test.domain.Zgoods;
import com.ruoyi.test.domain.Zorder;
import com.ruoyi.test.mapper.ZgoodsMapper;
import com.ruoyi.test.mapper.ZorderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * Created by WangYangfan on 2020/11/3 9:12
 */
@Service
public class WeatherService {
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private MsgProducer msgProducer;
    @Autowired
    private ISysLogininforService iSysLogininforService;
    @Autowired
    private ZorderMapper zorderMapper;
    @Autowired
    private ZgoodsMapper zgoodsMapper;

    private final SpinTestLock lock = SpinTestLock.getSpinTestLock();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    public boolean killGoodsRabbit() {
        boolean flag = false;
        Jedis jedis = jedisPool.getResource();
        try {
            if (jedis.exists(GOODS)) {
                synchronized (WeatherService.class){
                    Integer num = Integer.valueOf(jedis.get(GOODS));
                    if (num > 0) {
                        jedis.set(GOODS, String.valueOf(num - 1));
                        flag = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        if (flag) {
            msgProducer.sendMsgB("下单成功");
        }
        return flag;
    }

    @Transactional
    public boolean killGoods() {
        boolean flag = false;
        Zgoods zgoods = zgoodsMapper.selectZgoodsById(1L);
        if (zgoods.getNum() > 0) {
            String orderId = UUID.randomUUID().toString();
            Zorder zorder = new Zorder();
            zorder.setName("小米10");
            zorder.setOrderId(orderId);
            zorder.setNum(1);
            zorderMapper.insertZorder(zorder);
            zgoodsMapper.killZgoods(1L);
            flag = true;
        }
        return flag;
    }

    public Object[] selectWeather(String province, String city, String area, String data) {
        Object[] objects = new Object[4];
        String host = "https://iweather.market.alicloudapi.com";
        String path = "/address";
        String method = "GET";
        String appcode = "b3f02b20f86d4ee0a41167ca372b998c";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<>();
        querys.put("prov", province);
        querys.put("city", city);
        querys.put("area", area);
//        querys.put("date", "202010");
        querys.put("needday", "7");

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            String string = JSON.toJSONString(querys);
            String buildUrl = HttpUtils.buildUrl(host, path, querys);
            String string1 = "?area=";
            String response = HttpUtils.sendGet(buildUrl, headers);
//            HttpResponse response = HttpUtils.sendGet(host, path, method, headers, querys);

            HashMap hashMap1 = JSON.parseObject(response, HashMap.class);
            Object data1 = hashMap1.get("data");
            HashMap hashMap2 = JSON.parseObject(data1.toString(), HashMap.class);
            Object data2 = hashMap2.get("day7");
            List<HashMap> maps = JSON.parseArray(data2.toString(), HashMap.class);
            List<Object> air = new ArrayList<>();
            List<Object> nightAir = new ArrayList<>();
            List<Object> date = new ArrayList<>();
            maps.forEach(x -> {
                air.add(x.get("day_air_temperature"));
                nightAir.add(x.get("night_air_temperature"));
                date.add("周" + x.get("week").toString());
            });

            objects[0] = date;
            objects[1] = air;
            objects[2] = nightAir;
            objects[3] = data2;
            System.out.println();
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objects;
    }

    @Transactional
    public Object[] selectWeatherRabbit(String province, String city, String area, String needday) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> querys = new TreeMap<>();
        querys.put("prov", province);
        querys.put("city", city);
        querys.put("area", area);
        querys.put("needday", needday);
        String key = "Weather" + "::" + JSON.toJSONString(querys);
        Object[] result = new Object[4];
        try {
            //mq记录日志
            msgProducer.sendMsgC(JSON.toJSONString(querys));
            if (jedis.exists(key)) {
                //有缓存  从redis缓存中获取json 之后还原对象返回
                String json = jedis.get(key);
                result = ObjectMapperUtil.toObject(json, Object[].class);
                logger.info("redis实现缓存查询!!!!");
            } else {
                String host = "https://iweather.market.alicloudapi.com";
                String path = "/address";
                String method = "GET";
                String appcode = "b3f02b20f86d4ee0a41167ca372b998c";
                Map<String, String> headers = new HashMap<String, String>();
                //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
                headers.put("Authorization", "APPCODE " + appcode);
                String buildUrl = HttpUtils.buildUrl(host, path, querys);
                String response = HttpUtils.sendGet(buildUrl, headers);
                HashMap hashMap1 = JSON.parseObject(response, HashMap.class);
                Object data1 = hashMap1.get("data");
                HashMap hashMap2 = JSON.parseObject(data1.toString(), HashMap.class);
                Object data2 = hashMap2.get("day7");
                List<HashMap> maps = JSON.parseArray(data2.toString(), HashMap.class);
                List<Object> air = new ArrayList<>();
                List<Object> nightAir = new ArrayList<>();
                List<Object> date = new ArrayList<>();
                maps.forEach(x -> {
                    air.add(x.get("day_air_temperature"));
                    nightAir.add(x.get("night_air_temperature"));
                    date.add("周" + x.get("week").toString());
                });
                result[0] = date;
                result[1] = air;
                result[2] = nightAir;
//                result[3] = data2;
                jedis.set(key, JSON.toJSONString(result));
            }
        } catch (Exception e) {
//            result = null;
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
        return result;
    }

    private static final String GOODS = "goods";

}
