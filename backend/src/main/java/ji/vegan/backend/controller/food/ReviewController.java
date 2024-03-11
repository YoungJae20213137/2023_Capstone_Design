package ji.vegan.backend.controller.food;

import com.querydsl.core.types.Predicate;
import io.micrometer.common.util.StringUtils;
import ji.vegan.backend.bean.dto.ReviewCreate;
import ji.vegan.backend.bean.food.QReview;
import ji.vegan.backend.bean.food.Review;
import ji.vegan.backend.bean.food.Shop;
import ji.vegan.backend.bean.user.LoginUser;
import ji.vegan.backend.bean.user.UserInfo;
import ji.vegan.backend.service.FileUploadService;
import ji.vegan.backend.service.food.ReviewService;
import ji.vegan.backend.service.food.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/review")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private static final String UPLOAD_PATH = "_review";
    private final FileUploadService fileUploadService;
    private final ReviewService reviewService;
    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<Page<Review>> lists(@QuerydslPredicate(root = Review.class) Predicate predicate,
                                              @PageableDefault(sort = "id") Pageable pageable) {
        Page<Review> page = reviewService.findAll(predicate, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> detail(@PathVariable long id) {
        Review review = reviewService.findById(id);
        return ResponseEntity.ok(review);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Review> insert(@LoginUser UserInfo userInfo,
                                         @RequestPart("data") ReviewCreate reviewCreate,
                                         @RequestPart(name = "image1", required = false) MultipartFile image1) {

        if (StringUtils.isEmpty(reviewCreate.content())) {
            log.warn("review content is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] review content is empty error. ");
        }

        if (StringUtils.isEmpty(reviewCreate.rating())) {
            log.warn("review rating is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] review rating is empty error. ");
        }

        Shop shop = shopService.findById(reviewCreate.shopId());

        Review review = new Review();
        review.setShop(shop);
        review.setContent(reviewCreate.content());
        review.setRating(reviewCreate.rating());
        review.setAdvantages(reviewCreate.advantages());
        review.setDisadvantage(reviewCreate.disadvantage());
        review.setUserId(userInfo.getUserId());
        review.setCreatedAt(ZonedDateTime.now());

        String uploadPath = UPLOAD_PATH;
        if (null != image1) {
            String image1String = fileUploadService.fileUpload(uploadPath, image1);
            review.setImage1(image1String);
        }

        Review inserted = reviewService.save(review);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/review/{id}")
                .buildAndExpand(review.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(inserted);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Review> modify(@LoginUser UserInfo userInfo,
                                         @PathVariable long id,
                                         @RequestPart("data") ReviewCreate reviewCreate,
                                         @RequestPart(name = "image1", required = false) MultipartFile image1) {


        if (StringUtils.isEmpty(reviewCreate.content())) {
            log.warn("review content is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] review content is empty error. ");
        }

        if (StringUtils.isEmpty(reviewCreate.rating())) {
            log.warn("review rating is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] review rating is empty error. ");
        }

        Shop shop = shopService.findById(reviewCreate.shopId());
        Review review = reviewService.findById(id);

        if (null != image1) {
            if (StringUtils.isNotEmpty(review.getImage1())) {
                fileUploadService.deleteFiles(UPLOAD_PATH, List.of(review.getImage1()));
            }

            String image1String = fileUploadService.fileUpload(UPLOAD_PATH, image1);
            review.setImage1(image1String);
        }

        review.setShop(shop);
        review.setContent(reviewCreate.content());
        review.setRating(reviewCreate.rating());
        review.setAdvantages(reviewCreate.advantages());
        review.setDisadvantage(reviewCreate.disadvantage());
        review.setUserId(userInfo.getUserId());

        Review updated = reviewService.save(review);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<Iterable<Review>> delete(@LoginUser UserInfo userInfo,
                                                   @PathVariable List<Long> ids) {
        Predicate query = QReview.review.id.in(ids);
        final Iterable<Review> deleteList = reviewService.findAll(query);

        for (Review review : deleteList) {
            if (StringUtils.isNotEmpty(review.getImage1())) {
                fileUploadService.deleteFiles(UPLOAD_PATH, List.of(review.getImage1()));
            }
        }

        reviewService.delete(deleteList);
        return ResponseEntity.ok(deleteList);
    }
}
