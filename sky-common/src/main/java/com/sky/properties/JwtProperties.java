package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.jwt") // 配置文件中sky.jwt开头的属性会映射到这个类中
@Data
/*
* @ConfigurationProperties(prefix = "sky.jwt") 的作用是
* 将外部配置（例如 application.properties 或 application.yml）中以 sky.jwt 为前缀的属性，
* 按照属性名映射并注入到当前类的字段上（类型转换和松散绑定会自动处理）。
* 因为类上也有 @Component，这个类会作为 Spring Bean 注册到容器中，其他地方可以直接注入使用。
* */

/**
 * 因为配置类只是“映射/承载”配置值，真正的配置来源应放在外部，原因包括：
 * 外部化配置：修改 application.yml 或环境变量可在不重编译、不重启（或少量重启）的情况下调整行为，便于不同环境（dev/test/prod）使用不同值。
 * 运维与安全：密钥等敏感信息可以由运维通过环境变量、配置中心或密钥存储注入，避免写死在代码里。
 * Spring 特性：@ConfigurationProperties 把外部配置绑定到 JwtProperties，并支持松散绑定、类型转换、校验（可加 @Validated）。
 * 覆盖与优先级：Spring Boot 支持多来源配置（application.yml、环境变量、命令行、Config Server），按优先级覆盖，灵活调度。
 * 可测试与可复用：测试时可以替换配置文件或注入不同值，同一配置类可在多环境复用。
 * 简单说：配置类负责读取与类型安全地持有配置，具体值应该放在 application.yml / 环境变量 / 配置中心等外部位置，以便运维、部署与安全管理。
 */

public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
