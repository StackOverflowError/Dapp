/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.config.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.asiainfo.iboss.lcmbass.app.component.MyMetaObjectHandler;
import com.asiainfo.iboss.lcmbass.app.utils.IbossEncryDecryUtils;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;

import lombok.Cleanup;

/**
 * @Description:
 * @author :lenovo
 * @date :2019年6月17日 下午8:34:03
 */
@Configuration
public class DataSourceInitConfig  implements EnvironmentAware,ApplicationContextAware,BeanDefinitionRegistryPostProcessor{
	
	private static transient Logger log=LoggerFactory.getLogger(DataSourceInitConfig.class);

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
	
	private Properties properties;
	
	private Environment environment;
		
	@Override
	public void setEnvironment(Environment environment) {
		this.environment=environment;
		
		String[] activeProfiles = environment.getActiveProfiles();
		if(activeProfiles!=null && activeProfiles.length>0) {
			String propertiesPath="application.properties";
			try {
				//boot项目不用加载这个东西，environment里面就有
				
				//一般项目中，都是根据环境查找的
				Resource resource = new ClassPathResource(propertiesPath);
				Properties props = PropertiesLoaderUtils.loadProperties(resource);
				this.properties=props;
			} catch (IOException e) {
				log.warn("未找到文件："+propertiesPath);
			}
		}
	}
	
	private String getProperty(String key) {
		String value = this.environment.getProperty(key);
		if(StringUtils.isBlank(value) && this.properties!=null) {
			value=this.properties.getProperty(key);
		}
		log.debug("通过环境获取参数：key="+key+" ,value="+value);
		return value;
	}

	public static final String S_dataSourceId="DATA_SOURCE_ID";

	/**
	 * 动态创建bean并注入到容器中，通过Set方法
	 * 
	 * @param applicationContext 配置上下文
	 * @param name               beanName
	 * @param clazz              对象类
	 * @param properties         属性列表key:属性名 value:属性值
	 * @param refMap             key:属性值 value：ref BeanName
	 */
	public static <T> T registerBean(ApplicationContext applicationContext,BeanDefinitionRegistry regist, String name, Class<T> clazz,
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

		BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		regist.registerBeanDefinition(name, beanDefinition);

		try {
			return applicationContext.getBean(name, clazz);
		} catch (Exception e) {
			return applicationContext.getBean("&" + name, clazz);
		}
	}
	

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
	}
	
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		
		//生成Bean
		paginationInterceptor();
		plugins();
		
		
		//注册NG_IBOSS_PSWD_01
		registerPswdDataSource(registry,applicationContext);
		Map<String,Map<String,String>> dataSourceConfigMap=null;

		try {
			dataSourceConfigMap = getDataSourceConfig();
		} catch (Exception e) {
			log.error("读取数据库配置出错："+e.getMessage());
			throw new RuntimeException(e);
		}
		
		if(dataSourceConfigMap.size()>0) {
			for(String key:dataSourceConfigMap.keySet()) {
				Map<String,String> config=dataSourceConfigMap.get(key);
				String name = config.get(S_dataSourceId);
				config.remove(S_dataSourceId);
				
				log.info("加载数据源 【{}】开始",name);
				registerBean(applicationContext,registry, name, DruidDataSource.class, config,null);
				
				registerBean(applicationContext,registry,name+"_myMetaObjectHandler", MyMetaObjectHandler.class, null,null);
				
				Map<String,String> globalRefMap=new HashMap<>();
				globalRefMap.put("metaObjectHandler", name+"_myMetaObjectHandler");
				registerBean(applicationContext,registry,name+"_GlobalConfiguration", GlobalConfig.class, null,globalRefMap);
				
				Map<String,String> sqlSessionProperties=new HashMap<>();
				String jdbcPackageName=StringUtils.substringAfter(name, "NG_IBOSS_");
				jdbcPackageName=StringUtils.remove(jdbcPackageName, "_");
				jdbcPackageName=StringUtils.lowerCase(jdbcPackageName);
				sqlSessionProperties.put("typeAliasesPackage", "com.asiainfo.iboss.lcmbass.app.dao."+jdbcPackageName+".po");
				
				Map<String,String> sqlSessionRefMap=new HashMap<>();
				sqlSessionRefMap.put("dataSource", name);
				sqlSessionRefMap.put("plugins", "plugins");
				sqlSessionRefMap.put("globalConfig", name+"_GlobalConfiguration");
				
				registerBean(applicationContext,registry,name+"_Mybatis_sqlSessionFactoryBean", MybatisSqlSessionFactoryBean.class, sqlSessionProperties,sqlSessionRefMap);
				
				Map<String, String> mapperProperties=new HashMap<>();
				mapperProperties.put("basePackage", "com.asiainfo.iboss.lcmbass.app.dao."+jdbcPackageName+".mapper");
				mapperProperties.put("sqlSessionFactoryBeanName", name+"_Mybatis_sqlSessionFactoryBean");
				
				registerBean(applicationContext,registry,name+"_Mybatis_mapperScannerConfigurer", MapperScannerConfigurer.class, mapperProperties, null);
				
				Map<String,String> transcationManagerRefMap=new HashMap<>();
				transcationManagerRefMap.put("dataSource", name);
				registerBean(applicationContext,registry,name+"_Mybatis_transactionManager", DataSourceTransactionManager.class, null, transcationManagerRefMap);
				
				log.info("加载数据源 【"+name+"】完成");
			}
			log.info("合计加载数据源 {"+dataSourceConfigMap.size()+"} 个");
		}	
	}

	private static final String selectSql="select * from CFG_CBASS_DATASOURCE where REMOVE_TAG='1' and SYSCODE_LIST like ?" ;
	
	private Map<String, Map<String,String>> getDataSourceConfig() throws Exception {
		Map<String, Map<String,String>> dataSourceMap=new HashMap<>();
		selectDataSource2Map(dataSourceMap, "PUBLIC");
		String sysCode=System.getProperty("INTF_SYSCODE");
		selectDataSource2Map(dataSourceMap, sysCode);
		return dataSourceMap;
	}
	
	private void selectDataSource2Map(Map<String, Map<String,String>> dataSourceMap,String sysCode) throws Exception {
		DruidDataSource druidDataSource=(DruidDataSource) applicationContext.getBean("NG_IBOSS_PSWD_01");
		@Cleanup
		Connection conn = druidDataSource.getConnection();
		@Cleanup
		PreparedStatement ps = conn.prepareStatement(selectSql);
		ps.setString(1, "%"+sysCode+"%");
		@Cleanup
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			Map<String,String> configMap=new HashMap<>();
			String jndiName=rs.getString("DATASOURCE_NAME");
			String url=rs.getString("DB_CONN_URL");
			String userName=rs.getString("DB_USER_NAME");
			String password=rs.getString("DB_PASS_WORD");
			if(StringUtils.endsWith(password, "==")) {
				password=IbossEncryDecryUtils.druidDecry(password);
			}
			
			String driverClassName=rs.getString("DB_DRIVER_CLASS_NAME");
			String filters=rs.getString("FILTER_NAME");
			String maxActive=rs.getString("MAX_ACTIVE");
			String initialSize=rs.getString("INITIAL_SIZE");
			String minIdle=rs.getString("MIN_IDLE");
			String maxWait=rs.getString("MAX_WAIT");
			String timeBetweenEvictionRunsMillis=rs.getString("TIME_EVICTION_RUNS_MILLIS");
			String minEvictableIdleTimeMillis=rs.getString("MIN_EVICTABLE_IDLE_TIME_MILLIS");
			String testWhileIdle=changeIntStr2BoolStr(rs.getString("TEST_WHILE_IDLE"));
			String testOnBorrow=changeIntStr2BoolStr(rs.getString("TEST_ON_BORROW"));
			String testOnReturn=changeIntStr2BoolStr(rs.getString("TEST_ON_RETURN"));
			String poolPreparedStatements=changeIntStr2BoolStr(rs.getString("POOL_PREPARED_STATEMENTS"));
			String maxPoolPreparedStatementPerConnectionSize=rs.getString("MAX_PSCACHE_SIZE");
			String defaultAutoCommit=changeIntStr2BoolStr(rs.getString("DEFAULT_AUTO_COMMINT"));
			String removeAbandoned=changeIntStr2BoolStr(rs.getString("REMOVE_ABANDONED"));
			String removeAbandonedTimeout=rs.getString("REMOVE_ABANDONED_TIMEOUT");
			String logAbandoned=changeIntStr2BoolStr(rs.getString("LOG_ABANDONED"));
			String validationQuery=rs.getString("VALIDATION_QUERY");

			
			configMap.put(S_dataSourceId, jndiName);
			configMap.put("url",url);
			configMap.put("username",userName);
			configMap.put("password",password);
			configMap.put("driverClassName",driverClassName);
			configMap.put("filters",filters);
			configMap.put("maxActive",maxActive);
			configMap.put("initialSize",initialSize);
			configMap.put("minIdle",minIdle);
			configMap.put("maxWait",maxWait);
			configMap.put("timeBetweenEvictionRunsMillis",timeBetweenEvictionRunsMillis);
			configMap.put("minEvictableIdleTimeMillis",minEvictableIdleTimeMillis);
			configMap.put("testWhileIdle",testWhileIdle);
			configMap.put("testOnBorrow",testOnBorrow);
			configMap.put("testOnReturn",testOnReturn);
			configMap.put("poolPreparedStatements",poolPreparedStatements);
			configMap.put("maxPoolPreparedStatementPerConnectionSize",maxPoolPreparedStatementPerConnectionSize);
			configMap.put("defaultAutoCommit",defaultAutoCommit);
			configMap.put("removeAbandoned",removeAbandoned);
			configMap.put("removeAbandonedTimeout",removeAbandonedTimeout);
			configMap.put("logAbandoned",logAbandoned);
			configMap.put("validationQuery",validationQuery);
			
			dataSourceMap.put(jndiName, configMap);
		}
	}	
	
	public void registerPswdDataSource(BeanDefinitionRegistry registry,ApplicationContext applicationContext){
		
		Map<String, String> configMap=new HashMap<>();

		String userName=getProperty("pswd01.jdbc.username");
		String password = getProperty("pswd01.jdbc.password");
		if(StringUtils.isBlank(password)) {
			log.error("在配置文件中未获取到有效的参数pswd01.jdbc.password，请配置，退出！");
			return;
		}
		if(StringUtils.endsWith(password, "==")) {
			try {
				password=IbossEncryDecryUtils.druidDecry(password);
			} catch (Exception e) {
				log.error("解密出错："+e);
				throw new RuntimeException(e);
			}
		}
		
		configMap.put("url",getProperty("pswd01.jdbc.url"));
		configMap.put("username",userName);
		configMap.put("password",password);
		configMap.put("driverClassName",getProperty("pswd01.jdbc.driver"));
		configMap.put("filters","config,stat,mergeStat");
		configMap.put("maxActive",getProperty("pswd01.jdbc.maxActive"));
		configMap.put("initialSize",getProperty("pswd01.jdbc.initialSize"));
		configMap.put("minIdle","0");
		configMap.put("maxWait",getProperty("pswd01.jdbc.maxWait"));
		configMap.put("timeBetweenEvictionRunsMillis","60000");
		configMap.put("minEvictableIdleTimeMillis","300000");
		configMap.put("testWhileIdle","true");
		configMap.put("testOnBorrow","false");
		configMap.put("testOnReturn","false");
		configMap.put("poolPreparedStatements","true");
		configMap.put("maxPoolPreparedStatementPerConnectionSize","100");
		configMap.put("defaultAutoCommit","true");
		configMap.put("removeAbandoned","true");
		configMap.put("removeAbandonedTimeout","60");
		configMap.put("logAbandoned","true");
		configMap.put("validationQuery","select 1 from dual");
		
		registerBean(applicationContext,registry,"NG_IBOSS_PSWD_01", DruidDataSource.class, configMap,null);
		
	}
	
	
	@Bean(name="paginationInterceptor")
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		paginationInterceptor.setDialectType("oracle");
		return paginationInterceptor;
		
	}
	
	@Bean(name="plugins")
	public Interceptor[] plugins() {
		Interceptor []plugins=new Interceptor[1];
		plugins[0]=paginationInterceptor();
		return plugins;
	}
	
	public static String changeIntStr2BoolStr(String intStr) {
		if(StringUtils.equals("1", intStr)) {
			return "true";
		}
		return "false";
	}

}
