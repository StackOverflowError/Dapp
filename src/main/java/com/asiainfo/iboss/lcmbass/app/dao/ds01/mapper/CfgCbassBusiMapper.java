package com.asiainfo.iboss.lcmbass.app.dao.ds01.mapper;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 业务接口配置表 Mapper 接口
 * </p>
 *
 * @author asys
 * @since 2020-09-08
 */
@Component
public interface CfgCbassBusiMapper extends BaseMapper<CfgCbassBusi> {
    public List<CfgCbassBusi> getInfoByCommondCode(@Param("commondCode")String commondCode);

}
