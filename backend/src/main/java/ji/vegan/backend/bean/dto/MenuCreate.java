package ji.vegan.backend.bean.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;

@Builder
public record MenuCreate(
        long shopId,
        String name,
        int price,
        String description,
        String tags,

        ObjectNode categories
) {
}
