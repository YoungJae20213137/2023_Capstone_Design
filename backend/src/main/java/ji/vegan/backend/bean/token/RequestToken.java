package ji.vegan.backend.bean.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class RequestToken {
    private String userId;
    private String password;
}
