package ji.vegan.backend.bean.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ReviewCreate(
        long shopId,
        String content,
        String rating,
        List<String> advantages,
        List<String> disadvantage
) {
}
