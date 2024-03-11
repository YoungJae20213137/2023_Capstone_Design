package ji.vegan.backend.service.food;

import com.querydsl.core.types.Predicate;
import ji.vegan.backend.bean.food.Shop;
import ji.vegan.backend.repository.food.ShopRepository;
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
public class ShopService implements IService<Shop> {
    private final ShopRepository shopRepository;

    @Override
    public Page<Shop> findAll(Predicate predicate, Pageable pageable) {
        return shopRepository.findAll(predicate, pageable);
    }

    @Override
    public Iterable<Shop> findAll(Predicate predicate) {
        return shopRepository.findAll(predicate);
    }

    @Override
    public Shop findById(long id) {
        final Optional<Shop> byId = shopRepository.findById(id);

        if (byId.isEmpty()) {
            log.error("shop not exists. id : {}", id);
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] shop not exists. id = " + id);
        }

        return byId.get();
    }

    @Override
    public Optional<Shop> findOne(Predicate predicate) {
        return shopRepository.findOne(predicate);
    }

    @Override
    public boolean exist(Predicate predicate) {
        return shopRepository.exists(predicate);
    }

    @Override
    public Shop save(Shop shop) {
        return shopRepository.save(shop);
    }

    @Override
    public void delete(Iterable<Shop> shops) {
        shopRepository.deleteAll(shops);
    }

    @Override
    public long count(Predicate predicate) {
        return shopRepository.count(predicate);
    }
}
