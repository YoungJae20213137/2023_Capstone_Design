package ji.vegan.backend.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom-config")
@Getter
@Setter
@ToString
public class CustomConfig {
    private String signKey;
    private int tokenExpireHour = 48;
    private String fileRootPath;
}
