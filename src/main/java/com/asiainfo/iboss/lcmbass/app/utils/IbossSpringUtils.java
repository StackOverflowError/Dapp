/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.utils;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 
 * <p>
 * Title: IbossSpringUtils.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: 亚信科技
 * </p>
 * 
 * @author asys
 * @date 2018年4月26日
 * @version
 */
@Component("ibossSpringUtils")
public class IbossSpringUtils implements BeanFactoryAware {

	/**
	 * @Description: 通过SringbeanName或类全名获取bean
	 * @param beanName           bean名字/类全名
	 * @param clazz              返回bean类型
	 * @param applicationContext spring上下文
	 * @return
	 * @author lenovo
	 * @return
	 * @date 2019年4月18日 下午5:34:03
	 */
	public static <T> T getBeanByTypeAndName(String beanName, Class<T> clazz, ApplicationContext applicationContext) {
		Map<String, T> runableBean = applicationContext.getBeansOfType(clazz);
		T task = runableBean.get(beanName);
		if (task == null) {
			// 忽略大小写获取处理类
			for (String runableBeanName : runableBean.keySet()) {
				String className = runableBean.get(runableBeanName).getClass().getName();
				if (StringUtils.equalsIgnoreCase(beanName, runableBeanName)
						|| StringUtils.equals(className, beanName)) {
					task = runableBean.get(runableBeanName);
					break;
				}
			}
		}
		return task;

	}

	private static BeanFactory beanFactory;

	// public static Object getBean(String beanName) {
	// return ContextLoader.getCurrentWebApplicationContext().getBean(beanName);
	// }

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		IbossSpringUtils.beanFactory = beanFactory;

	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		if (null != beanFactory) {
			return (T) beanFactory.getBean(beanName);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBeanByClassFullName(String classFullName, BeanFactory beanFactory) {
		Class<T> clazz;
		try {
			clazz = (Class<T>) Class.forName(classFullName);
		} catch (ClassNotFoundException e) {
			return null;
		}
		if (null != beanFactory) {
			return beanFactory.getBean(clazz);
		}
		return null;
	}
	/**
	 * @Description: 通过class类型，获取Spring容器中的bean
	 * @param clazz              返回bean类型
	 * @return
	 * @author lenovo
	 * @return
	 * @date 2019年4月18日 下午5:34:03
	 */
	public static <T> T getBeanByClass(Class<T> clazz) {
		return beanFactory.getBean(clazz);
	}

	/**
	 * 动态创建bean并注入到容器中，通过构造方法
	 */
	public static <T> T registerBean(ConfigurableApplicationContext applicationContext, String name, Class<T> clazz,
			Object... args) {
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		if (args.length > 0) {
			for (Object arg : args) {
				beanDefinitionBuilder.addConstructorArgValue(arg);
			}
		}
		BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

		BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
		beanFactory.registerBeanDefinition(name, beanDefinition);
		return applicationContext.getBean(name, clazz);
	}

	/**
	 * 动态创建bean并注入到容器中，通过Set方法
	 * 
	 * @param applicationContext 配置上下文
	 * @param name               beanName
	 * @param clazz              对象类
	 * @param properties         属性列表key:属性名 value:属性值
	 * @param refMap             key:属性值 value：ref BeanName
	 */
	public static <T> T registerBean(ConfigurableApplicationContext applicationContext, String name, Class<T> clazz,
			Map<String, String> properties, Map<String, String> refMap) {
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		if (properties != null && properties.size() > 0) {
			for (String key : properties.keySet()) {
				beanDefinitionBuilder.addPropertyValue(key, properties.get(key));
			}
		}
		if (refMap != null && refMap.size() > 0) {
			for (String key : refMap.keySet()) {
				beanDefinitionBuilder.addPropertyReference(key, refMap.get(key));
			}
		}


		BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

		BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
		beanFactory.registerBeanDefinition(name, beanDefinition);

		try {
			return applicationContext.getBean(name, clazz);
		} catch (Exception e) {
			return applicationContext.getBean("&" + name, clazz);
		}
	}

}
