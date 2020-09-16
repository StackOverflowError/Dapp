package com.asiainfo.iboss.lcmbass.app.service.impl;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassThd;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.mapper.CfgCbassThdMapper;
import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassThdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 线程配置表
1)多生产者-多消费者 服务实现类
 * </p>
 *
 * @author asys
 * @since 2020-09-08
 */
@Service
public class CfgCbassThdServiceImpl extends ServiceImpl<CfgCbassThdMapper, CfgCbassThd> implements CfgCbassThdService {

}
