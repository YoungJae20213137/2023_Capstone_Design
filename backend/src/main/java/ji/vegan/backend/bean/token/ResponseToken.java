package ji.vegan.backend.bean.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import ji.vegan.backend.bean.user.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ResponseToken {
    private String accessToken;
    private String refreshToken;
    private Role role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime issue;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime expire;

    public ResponseToken(String accessToken, String refreshToken, ZonedDateTime issue, ZonedDateTime expire, Role role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.issue = issue;
        this.expire = expire;
        this.role = role;
    }
}
