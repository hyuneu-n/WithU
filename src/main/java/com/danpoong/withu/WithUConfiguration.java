//package com.danpoong.withu;
//
//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.servers.Server;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@OpenAPIDefinition(servers = {@Server(url = "/",
//        description = "Default Server URL")})
//
//@Configuration
//public class WithUConfiguration implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins(
//                        "http://localhost:3000",
//                        "http://localhost:8080",
//                        "http://ec2-13-209-177-17.ap-northeast-2.compute.amazonaws.com")
//                .allowedMethods("GET", "POST", "PATCH", "DELETE", "PUT")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
//}