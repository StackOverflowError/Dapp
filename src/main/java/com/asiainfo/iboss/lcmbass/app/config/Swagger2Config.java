package com.asiainfo.iboss.lcmbass.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Description: Swagger2配置类
 * @author :lenovo
 * @date :2019年3月14日 上午10:06:44
 */
@EnableSwagger2
@Configuration
public class Swagger2Config{

	@Value("${swagger.basepackage}")
	private String basePackage="com.iboss";
	
	@Value("${swagger.enable}")
	private boolean enableSwagger=false;
	
	@Bean
	public Docket createRestApi() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2);
		docket.apiInfo(getApiInfo());
		docket.enable(enableSwagger);//建议生产关闭
		ApiSelectorBuilder selectorBuilder = docket.select();
		selectorBuilder.apis(RequestHandlerSelectors.basePackage(basePackage));
		selectorBuilder.paths(PathSelectors.any());
		return selectorBuilder.build();
	}
	
	@SuppressWarnings("deprecation")
	private ApiInfo getApiInfo() {
		ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
		apiInfoBuilder.title("iboss restful api");
		apiInfoBuilder.description("本页展示iboss restful api ,想看更多，请联系管理员！");
		apiInfoBuilder.version("1.0");
		apiInfoBuilder.contact("admin");        
		
		return apiInfoBuilder.build();
	}
	
}

