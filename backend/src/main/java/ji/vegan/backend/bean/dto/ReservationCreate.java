package ji.vegan.backend.bean.dto;

import lombok.Builder;

@Builder
public record ReservationCreate(
        long userId, // 사용자Id
        long shopId, // 음식점Id
        String startDate, // 예약 날짜 ex) 2024-01-27
        String startTime, // 예약 시간 ex) 12:00
        int person, // 사람 수
        String option // 옵션 ex) 순대 많이 넣어주세요
) {
}
