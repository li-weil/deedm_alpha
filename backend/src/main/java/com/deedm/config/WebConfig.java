package com.deedm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置图片资源的访问 - 主要路径
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:./data/")
                .setCachePeriod(3600);

  
        // 备用：如果data目录不存在，也可以尝试临时目录
        registry.addResourceHandler("/api/temp-images/**")
                .addResourceLocations("file:./temp/")
                .setCachePeriod(3600);
    }
}