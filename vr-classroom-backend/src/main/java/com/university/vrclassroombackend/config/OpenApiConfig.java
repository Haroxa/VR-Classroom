package com.university.vrclassroombackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Value("${swagger.server.port:8082}")
    private String swaggerServerPort;
    
    @Bean
    public OpenAPI vrClassroomOpenAPI() {
        List<Server> servers = new ArrayList<>();
        
        // 负载均衡服务器（唯一入口）
        Server loadBalancerServer = new Server();
        loadBalancerServer.setUrl("http://localhost:" + swaggerServerPort);
        loadBalancerServer.setDescription("负载均衡入口");
        servers.add(loadBalancerServer);
        
        return new OpenAPI()
                .info(new Info()
                        .title("VR教室后端API文档")
                        .description("VR教室系统后端接口文档")
                        .version("v1.0.0")
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}

