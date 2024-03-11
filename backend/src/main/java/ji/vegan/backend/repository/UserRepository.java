package ji.vegan.backend.repository;

import com.querydsl.core.types.dsl.StringPath;
import ji.vegan.backend.bean.user.QUser;
import ji.vegan.backend.bean.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.lang.NonNull;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {
    @Override
    default void customize(QuerydslBindings bindings, @NonNull QUser root) {
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
    }
}
