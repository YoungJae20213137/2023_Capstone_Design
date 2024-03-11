package ji.vegan.backend.service;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IService<T> {
    public Page<T> findAll(Predicate predicate, Pageable pageable);

    public Iterable<T> findAll(Predicate predicate);

    public T findById(long id);

    public Optional<T> findOne(Predicate predicate);

    public boolean exist(Predicate predicate);

    public T save(T t);

    public void delete(Iterable<T> ts);

    public long count(Predicate predicate);
}
