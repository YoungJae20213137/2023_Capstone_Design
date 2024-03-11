package ji.vegan.backend.bean.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ShopCreate(
        String name,
        String rating,
        String phoneNumber,
        String address,
        String latitude,
        String longitude,
        String tags,
        List<String> times,
        String description

) {
}
