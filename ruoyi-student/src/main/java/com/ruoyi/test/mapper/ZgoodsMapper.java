package com.ruoyi.test.mapper;

import com.ruoyi.test.domain.Zgoods;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2020-11-03
 */
public interface ZgoodsMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public Zgoods selectZgoodsById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param zgoods 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Zgoods> selectZgoodsList(Zgoods zgoods);

    /**
     * 新增【请填写功能名称】
     * 
     * @param zgoods 【请填写功能名称】
     * @return 结果
     */
    public int insertZgoods(Zgoods zgoods);

    /**
     * 修改【请填写功能名称】
     * 
     * @param zgoods 【请填写功能名称】
     * @return 结果
     */
    public int updateZgoods(Zgoods zgoods);


    /**
     * 修改【请填写功能名称】
     *
     * @param id 【请填写功能名称】
     * @return 结果
     */
    public int killZgoods(Long id);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteZgoodsById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteZgoodsByIds(String[] ids);
}
