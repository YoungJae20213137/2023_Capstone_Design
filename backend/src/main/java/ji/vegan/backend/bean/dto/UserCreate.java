package ji.vegan.backend.bean.dto;

import ji.vegan.backend.bean.user.Role;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public record UserCreate(String userId,
                         String password,
                         Role role,
                         Map<String, String> favoriteShop) {
    public UserCreate {
        if (null == role) {
            role = Role.USER;
        }

        if (null == favoriteShop) {
            favoriteShop = new HashMap<>();
        }
    }
}
