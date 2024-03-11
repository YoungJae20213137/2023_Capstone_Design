package ji.vegan.backend.service.food;

import com.querydsl.core.types.Predicate;
import ji.vegan.backend.bean.food.Reservation;
import ji.vegan.backend.repository.food.ReservationRepository;
import ji.vegan.backend.service.IService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService implements IService<Reservation> {
    private final ReservationRepository reservationRepository;

    @Override
    public Page<Reservation> findAll(Predicate predicate, Pageable pageable) {
        return reservationRepository.findAll(predicate, pageable);
    }

    @Override
    public Iterable<Reservation> findAll(Predicate predicate) {
        return reservationRepository.findAll(predicate);
    }

    @Override
    public Reservation findById(long id) {
        final Optional<Reservation> byId = reservationRepository.findById(id);

        if (byId.isEmpty()) {
            log.error("reservation not exists. id : {}", id);
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] reservation not exists. id = " + id);
        }

        return byId.get();
    }

    @Override
    public Optional<Reservation> findOne(Predicate predicate) {
        return reservationRepository.findOne(predicate);
    }

    @Override
    public boolean exist(Predicate predicate) {
        return reservationRepository.exists(predicate);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public void delete(Iterable<Reservation> reservations) {
        reservationRepository.deleteAll(reservations);
    }

    @Override
    public long count(Predicate predicate) {
        return reservationRepository.count(predicate);
    }
}
