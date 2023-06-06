package org.whitefat.unmanned.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author liuyong
 * @version 1.0
 * @Description: Knife4j配置
 * @Createdate 2021/7/2 5:05 下午
 */
@Configuration
@EnableSwagger2
public class Knife4jConfig {

    @Bean(name = "apiDocket")
    public Docket apiDocket() {
        Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .termsOfServiceUrl("https://baidu.com/")
                        .contact(new Contact("liuyong", "", "liuyongfm@gmail.com"))
                        .title("unmanned")
                        .description("problem contact liuyongfm@gmail.com")
                        .version("1.0")
                        .build())
                //分组名称
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("org.whitefat.unmanned.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }



}
