package ji.vegan.backend.controller.food;

import com.querydsl.core.types.Predicate;
import ji.vegan.backend.bean.dto.ReservationCreate;
import ji.vegan.backend.bean.food.Menu;
import ji.vegan.backend.bean.food.QReservation;
import ji.vegan.backend.bean.food.Reservation;
import ji.vegan.backend.bean.food.Shop;
import ji.vegan.backend.bean.user.LoginUser;
import ji.vegan.backend.bean.user.User;
import ji.vegan.backend.bean.user.UserInfo;
import ji.vegan.backend.service.UserService;
import ji.vegan.backend.service.food.ReservationService;
import ji.vegan.backend.service.food.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@Slf4j
@RequiredArgsConstructor
public class ReservationController {

    private final UserService userService;
    private final ShopService shopService;

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<Page<Reservation>> lists(@QuerydslPredicate(root = Menu.class) Predicate predicate,
                                                   @PageableDefault(sort = "id") Pageable pageable) {
        Page<Reservation> page = reservationService.findAll(predicate, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> detail(@PathVariable long id) {
        Reservation reservation = reservationService.findById(id);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping()
    public ResponseEntity<Reservation> insert(@LoginUser UserInfo userInfo,
                                              @RequestBody() ReservationCreate reservationCreate) {


        User user = userService.findById(reservationCreate.userId());
        Shop shop = shopService.findById(reservationCreate.shopId());

        /* 음식점 하루에 1개의 예약만 가능 */
        if (reservationService.exist(
                QReservation.reservation.user.id.eq(reservationCreate.userId())
                        .and(QReservation.reservation.startDate.eq(reservationCreate.startDate()))
                        .and(QReservation.reservation.shop.id.eq(reservationCreate.shopId())))) {
            log.warn("Only one reservation is allowed shop.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] Only one reservation is allowed shop.");
        }

        /* 같은 예약 시간 확인 */
        if (reservationService.exist(
                QReservation.reservation.user.id.eq(reservationCreate.userId())
                        .and(QReservation.reservation.startDate.eq(reservationCreate.startDate()))
                        .and(QReservation.reservation.startTime.eq(reservationCreate.startTime())))) {
            log.warn("reservation is duplicate error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] reservation is duplicate error. ");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setShop(shop);
        reservation.setStartDate(reservationCreate.startDate());
        reservation.setStartTime(reservationCreate.startTime());
        reservation.setPerson(reservationCreate.person());
        reservation.setOption(reservationCreate.option());
        reservation.setCreatedAt(ZonedDateTime.now());

        Reservation inserted = reservationService.save(reservation);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/reservation/{id}")
                .buildAndExpand(reservation.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(inserted);
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<Iterable<Reservation>> delete(@LoginUser UserInfo userInfo,
                                                        @PathVariable List<Long> ids) {

        final Iterable<Reservation> deleteList = reservationService.findAll(QReservation.reservation.id.in(ids));

        reservationService.delete(deleteList);
        return ResponseEntity.ok(deleteList);
    }
}
