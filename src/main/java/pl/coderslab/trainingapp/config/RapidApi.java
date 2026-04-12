package pl.coderslab.trainingapp.config;

import lombok.Getter;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "api.rapid")
public class RapidApi {

    private String host;
    private String url;
    private String key;
}