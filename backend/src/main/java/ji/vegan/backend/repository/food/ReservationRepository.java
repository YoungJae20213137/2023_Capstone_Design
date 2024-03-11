package ji.vegan.backend.repository.food;

import com.querydsl.core.types.dsl.StringPath;
import ji.vegan.backend.bean.food.QReservation;
import ji.vegan.backend.bean.food.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.lang.NonNull;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, QuerydslPredicateExecutor<Reservation>, QuerydslBinderCustomizer<QReservation> {
    @Override
    default void customize(QuerydslBindings bindings, @NonNull QReservation root) {
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
    }
}

