package ji.vegan.backend.repository.food;

import com.querydsl.core.types.dsl.StringPath;
import ji.vegan.backend.bean.food.QShop;
import ji.vegan.backend.bean.food.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.lang.NonNull;

public interface ShopRepository extends JpaRepository<Shop, Long>, QuerydslPredicateExecutor<Shop>, QuerydslBinderCustomizer<QShop> {
    @Override
    default void customize(QuerydslBindings bindings, @NonNull QShop root) {
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
    }
}

