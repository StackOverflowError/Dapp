/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.component;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
/**
 * @Description:自动填充表字段
 * @author :asys
 * @date :2019年4月12日 上午11:29:45
 */
public class MyMetaObjectHandler implements MetaObjectHandler{

	/**
	 * 新增时填充
	 */
	@Override
	public void insertFill(MetaObject metaObject) {
		
		setFieldValByName("inserttime", new Date(), metaObject);
		setFieldValByName("updatetime", new Date(), metaObject);
		
		setFieldValByName("insertTime", new Date(), metaObject);
		setFieldValByName("updateTime", new Date(), metaObject);
	}

	/**
	 * 更新时填充
	 */
	@Override
	public void updateFill(MetaObject metaObject) {
		
		setFieldValByName("updatetime", new Date(), metaObject);
		setFieldValByName("updateTime", new Date(), metaObject);
		setFieldValByName("oprTime", new Date(), metaObject);
		
	}

}
