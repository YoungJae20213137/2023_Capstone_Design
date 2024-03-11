package ji.vegan.backend.controller.food;

import com.querydsl.core.types.Predicate;
import io.micrometer.common.util.StringUtils;
import ji.vegan.backend.bean.dto.ShopCreate;
import ji.vegan.backend.bean.food.*;
import ji.vegan.backend.bean.user.LoginUser;
import ji.vegan.backend.bean.user.UserInfo;
import ji.vegan.backend.service.FileUploadService;
import ji.vegan.backend.service.food.MenuService;
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
@RequestMapping("/api/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController {
    private static final String UPLOAD_PATH = "_shop";
    private final FileUploadService fileUploadService;
    private final ShopService shopService;
    private final MenuService menuService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<Shop>> lists(@QuerydslPredicate(root = Shop.class) Predicate predicate,
                                            @PageableDefault(sort = "id") Pageable pageable) {
        Page<Shop> page = shopService.findAll(predicate, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> detail(@PathVariable long id) {
        Shop shop = shopService.findById(id);
        return ResponseEntity.ok(shop);
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<?> detailMenu(@PathVariable long id) {
        Iterable<Menu> all = menuService.findAll(QMenu.menu.shop.id.eq(id));
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}/menu/count")
    public ResponseEntity<?> detailMenuCount(@PathVariable long id) {
        long count = menuService.count(QMenu.menu.shop.id.eq(id));
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<?> detailReview(@PathVariable long id) {
        Iterable<Review> all = reviewService.findAll(QReview.review.shop.id.eq(id));
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}/review/count")
    public ResponseEntity<?> detailReviewCount(@PathVariable long id) {
        long count = reviewService.count(QReview.review.shop.id.eq(id));
        return ResponseEntity.ok(count);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Shop> insert(@LoginUser UserInfo userInfo,
                                       @RequestPart("data") ShopCreate shopCreate,
                                       @RequestPart(name = "image1", required = false) MultipartFile image1,
                                       @RequestPart(name = "image2", required = false) MultipartFile image2) {

        if (StringUtils.isEmpty(shopCreate.name())) {
            log.warn("shop name is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] shop name is empty error. ");
        }

        if (shopCreate.times().isEmpty()) {
            log.warn("shop times is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] shop times is empty error. ");
        }

        /* 이름 존재 여부 체크 */
        if (shopService.exist(QShop.shop.name.eq(shopCreate.name()))) {
            log.warn("shop name is duplicate error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] shop name is duplicate error. ");
        }

        Shop shop = new Shop();
        shop.setName(shopCreate.name());
        shop.setRating(shopCreate.rating());
        shop.setPhoneNumber(shopCreate.phoneNumber());
        shop.setAddress(shopCreate.address());
        shop.setLatitude(shopCreate.latitude());
        shop.setLongitude(shopCreate.longitude());
        shop.setTimes(shopCreate.times());
        shop.setDescription(shopCreate.description());
        shop.setTags(shopCreate.tags());
        shop.setCreatedAt(ZonedDateTime.now());

        String uploadPath = UPLOAD_PATH;
        if (null != image1) {
            String image1String = fileUploadService.fileUpload(uploadPath, image1);
            shop.setImage1(image1String);
        }

        if (null != image2) {
            String image2String = fileUploadService.fileUpload(uploadPath, image2);
            shop.setImage2(image2String);
        }

        Shop inserted = shopService.save(shop);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/shop/{id}")
                .buildAndExpand(shop.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(inserted);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Shop> modify(@LoginUser UserInfo userInfo,
                                       @PathVariable long id,
                                       @RequestPart("data") ShopCreate shopCreate,
                                       @RequestPart(name = "image1", required = false) MultipartFile image1,
                                       @RequestPart(name = "image2", required = false) MultipartFile image2) {


        if (StringUtils.isEmpty(shopCreate.name())) {
            log.warn("shop name is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] shop name is empty error. ");
        }

        if (shopCreate.times().isEmpty()) {
            log.warn("shop times is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] shop times is empty error. ");
        }

        Shop shop = shopService.findById(id);

        String uploadPath = UPLOAD_PATH;
        if (null != image1) {
            if (StringUtils.isNotEmpty(shop.getImage1())) {
                fileUploadService.deleteFiles(uploadPath, List.of(shop.getImage1()));
            }

            String image1String = fileUploadService.fileUpload(uploadPath, image1);
            shop.setImage1(image1String);
        }

        if (null != image2) {
            if (StringUtils.isNotEmpty(shop.getImage2())) {
                fileUploadService.deleteFiles(uploadPath, List.of(shop.getImage2()));
            }

            String image2String = fileUploadService.fileUpload(uploadPath, image2);
            shop.setImage2(image2String);
        }

        shop.setName(shopCreate.name());
        shop.setPhoneNumber(shopCreate.phoneNumber());
        shop.setAddress(shopCreate.address());
        shop.setLatitude(shopCreate.latitude());
        shop.setLongitude(shopCreate.longitude());
        shop.setTimes(shopCreate.times());
        shop.setDescription(shopCreate.description());
        shop.setTags(shopCreate.tags());

        Shop updated = shopService.save(shop);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<Iterable<Shop>> delete(@LoginUser UserInfo userInfo,
                                                 @PathVariable List<Long> ids) {
        Predicate query = QShop.shop.id.in(ids);
        final Iterable<Shop> deleteList = shopService.findAll(query);

        for (Shop shop : deleteList) {
            if (StringUtils.isNotEmpty(shop.getImage1())) {
                fileUploadService.deleteFiles(UPLOAD_PATH, List.of(shop.getImage1()));
            }
            if (StringUtils.isNotEmpty(shop.getImage2())) {
                fileUploadService.deleteFiles(UPLOAD_PATH, List.of(shop.getImage2()));
            }
        }

        shopService.delete(deleteList);

        return ResponseEntity.ok(deleteList);
    }
}
