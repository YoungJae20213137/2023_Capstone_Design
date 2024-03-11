package ji.vegan.backend.repository.food;

import com.querydsl.core.types.dsl.StringPath;
import ji.vegan.backend.bean.food.QReview;
import ji.vegan.backend.bean.food.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.lang.NonNull;

public interface ReviewRepository extends JpaRepository<Review, Long>, QuerydslPredicateExecutor<Review>, QuerydslBinderCustomizer<QReview> {
    @Override
    default void customize(QuerydslBindings bindings, @NonNull QReview root) {
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
    }
}
