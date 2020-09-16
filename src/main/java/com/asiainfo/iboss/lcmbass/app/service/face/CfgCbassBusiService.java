package com.asiainfo.iboss.lcmbass.app.service.face;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 业务接口配置表 服务类
 * </p>
 *
 * @author asys
 * @since 2020-09-08
 */
public interface CfgCbassBusiService extends IService<CfgCbassBusi> {

    public List<CfgCbassBusi> getInfoByCommondCode(String commondCode);

}
