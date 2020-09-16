/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.starter;

/**
 * @Description: 线程、定时任务相关的starter必须实现该接口，否则不能管理启停
 * @author :lenovo
 * @date :2019年7月23日 下午5:45:49
 */
public interface IbossSupperStarter {
	
	/**
	 * 停止所有启动的内容
	 * */
	public void stopAll()  throws Exception;
	
	/**
	 * 启动所有需要启动的内容
	 * */
	public void startAll() throws Exception;

}
