package ji.vegan.backend.service.food;

import com.querydsl.core.types.Predicate;
import ji.vegan.backend.bean.food.Menu;
import ji.vegan.backend.repository.food.MenuRepository;
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
public class MenuService implements IService<Menu> {
    private final MenuRepository menuRepository;

    @Override
    public Page<Menu> findAll(Predicate predicate, Pageable pageable) {
        return menuRepository.findAll(predicate, pageable);
    }

    @Override
    public Iterable<Menu> findAll(Predicate predicate) {
        return menuRepository.findAll(predicate);
    }

    @Override
    public Menu findById(long id) {
        final Optional<Menu> byId = menuRepository.findById(id);

        if (byId.isEmpty()) {
            log.error("menu not exists. id : {}", id);
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] menu not exists. id = " + id);
        }

        return byId.get();
    }

    @Override
    public Optional<Menu> findOne(Predicate predicate) {
        return menuRepository.findOne(predicate);
    }

    @Override
    public boolean exist(Predicate predicate) {
        return menuRepository.exists(predicate);
    }

    @Override
    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public void delete(Iterable<Menu> menus) {
        menuRepository.deleteAll(menus);
    }

    @Override
    public long count(Predicate predicate) {
        return menuRepository.count(predicate);
    }
}
