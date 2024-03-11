package ji.vegan.backend.bean.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TokenRequest {
    private String accessToken;
    private String refreshToken;
}