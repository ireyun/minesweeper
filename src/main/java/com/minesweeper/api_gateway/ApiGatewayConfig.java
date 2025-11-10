package com.minesweeper.api_gateway;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API网关配置
 * 处理CORS跨域请求和静态资源映射
 */
@Configuration
public class ApiGatewayConfig implements WebMvcConfigurer {

    /**
     * 配置CORS跨域请求
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*") // 允许所有来源（生产环境应该指定具体域名）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Spring Boot默认会处理static目录下的资源
        // 这里可以添加额外的静态资源路径
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}

