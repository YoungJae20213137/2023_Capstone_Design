package ji.vegan.backend.bean.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfo {
    private long id;
    private String userId;
    private Role role;
    private String ip;

    public UserInfo() {
    }

    public UserInfo(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.role = user.getRole();
    }
}
