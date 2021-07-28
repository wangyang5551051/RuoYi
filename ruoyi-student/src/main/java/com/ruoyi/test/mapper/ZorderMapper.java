package com.ruoyi.test.mapper;

import com.ruoyi.test.domain.Zorder;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2020-11-03
 */
public interface ZorderMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public Zorder selectZorderById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param zorder 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Zorder> selectZorderList(Zorder zorder);

    /**
     * 新增【请填写功能名称】
     * 
     * @param zorder 【请填写功能名称】
     * @return 结果
     */
    public int insertZorder(Zorder zorder);

    /**
     * 修改【请填写功能名称】
     * 
     * @param zorder 【请填写功能名称】
     * @return 结果
     */
    public int updateZorder(Zorder zorder);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteZorderById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteZorderByIds(String[] ids);
}
