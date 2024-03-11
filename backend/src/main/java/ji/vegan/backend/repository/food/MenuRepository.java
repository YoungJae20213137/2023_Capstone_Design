package ji.vegan.backend.repository.food;

import com.querydsl.core.types.dsl.StringPath;
import ji.vegan.backend.bean.food.Menu;
import ji.vegan.backend.bean.food.QMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.lang.NonNull;

public interface MenuRepository extends JpaRepository<Menu, Long>, QuerydslPredicateExecutor<Menu>, QuerydslBinderCustomizer<QMenu> {
    @Override
    default void customize(QuerydslBindings bindings, @NonNull QMenu root) {
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
    }
}

