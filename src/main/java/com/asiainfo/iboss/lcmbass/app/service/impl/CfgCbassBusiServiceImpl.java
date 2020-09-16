package com.asiainfo.iboss.lcmbass.app.service.impl;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.mapper.CfgCbassBusiMapper;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;
import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassBusiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 业务接口配置表 服务实现类
 * </p>
 *
 * @author asys
 * @since 2020-09-08
 */
@Service
public class CfgCbassBusiServiceImpl extends ServiceImpl<CfgCbassBusiMapper, CfgCbassBusi> implements CfgCbassBusiService {


    @Autowired
    private CfgCbassBusiMapper cfgCbassBusiMapper;
    public List<CfgCbassBusi> getInfoByCommondCode(String commondCode){
        return cfgCbassBusiMapper.getInfoByCommondCode(commondCode);
    }

}
