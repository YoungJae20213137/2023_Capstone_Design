package ji.vegan.backend.service.food;

import com.querydsl.core.types.Predicate;
import ji.vegan.backend.bean.food.Review;
import ji.vegan.backend.repository.food.ReviewRepository;
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
public class ReviewService implements IService<Review> {
    private final ReviewRepository reviewRepository;

    @Override
    public Page<Review> findAll(Predicate predicate, Pageable pageable) {
        return reviewRepository.findAll(predicate, pageable);
    }

    @Override
    public Iterable<Review> findAll(Predicate predicate) {
        return reviewRepository.findAll(predicate);
    }

    @Override
    public Review findById(long id) {
        final Optional<Review> byId = reviewRepository.findById(id);

        if (byId.isEmpty()) {
            log.error("review not exists. id : {}", id);
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] review not exists. id = " + id);
        }

        return byId.get();
    }

    @Override
    public Optional<Review> findOne(Predicate predicate) {
        return reviewRepository.findOne(predicate);
    }

    @Override
    public boolean exist(Predicate predicate) {
        return reviewRepository.exists(predicate);
    }

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public void delete(Iterable<Review> reviews) {
        reviewRepository.deleteAll(reviews);
    }

    @Override
    public long count(Predicate predicate) {
        return reviewRepository.count(predicate);
    }
}
