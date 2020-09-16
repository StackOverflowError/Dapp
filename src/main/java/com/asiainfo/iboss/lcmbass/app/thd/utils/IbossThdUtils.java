/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.thd.utils;
import org.apache.commons.lang3.StringUtils;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author :lenovo
 * @date :2019年5月9日 下午2:46:01
 */
@Slf4j
public class IbossThdUtils {
	
	public static <T> EventFactory<T> getEventFactory(Class<T> clazz) {
		return new EventFactory<T>() {
			@Override
			public T newInstance() {
				try {
					return clazz.newInstance();
				} catch (Exception e) {
					log.error("反射生成对象出错，请确认对象名是否正确！");
				} 
				return null;
			}
		};
	}
	
	public static EventFactory<?> getEventFactory(String classFullName) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(classFullName);
		return getEventFactory(clazz);
	}
	public static EventFactory<?> getEventFactoryNoException(String classFullName){
		try {
			EventFactory<?> eventFactory = getEventFactory(classFullName);
			return eventFactory;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @Description:根据策略名获取对应的等待策略，如果不是SleepingWaitStrategy、BlockingWaitStrategy 则返回 YieldingWaitStrategy
	 * @author lenovo
	 * @date 2019年5月8日 上午9:45:00
	 */
	public static WaitStrategy getWaitStrategyByName(String strategyName) {
		if(StringUtils.equalsIgnoreCase("SleepingWaitStrategy", strategyName)) {
			return getSleepingWaitStrategy();
		}
		if(StringUtils.equalsIgnoreCase("BlockingWaitStrategy", strategyName)) {
			return getBlockingWaitStrategy();
		}
		return getYieldingWaitStrategy();
	}
	
	/**
	 * @Description:是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现；
	 * @author lenovo
	 * @date 2019年5月8日 上午9:45:00
	 */
	public static WaitStrategy getBlockingWaitStrategy() {
		return new BlockingWaitStrategy();
	}
	
	/**
	 * @Description:的性能表现跟 BlockingWaitStrategy 差不多，对 CPU 的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景；
	 * @author lenovo
	 * @date 2019年5月8日 上午9:45:35
	 */
	public static WaitStrategy getSleepingWaitStrategy() {
		return new SleepingWaitStrategy();
	}
	
	/**
	 * @Description:YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于 CPU 逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性。
	 * @author lenovo
	 * @date 2019年5月8日 上午9:46:28
	 */
	public static WaitStrategy getYieldingWaitStrategy() {
		return new YieldingWaitStrategy();

	}

}
