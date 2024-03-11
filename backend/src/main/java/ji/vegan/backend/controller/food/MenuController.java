package ji.vegan.backend.controller.food;

import com.querydsl.core.types.Predicate;
import io.micrometer.common.util.StringUtils;
import ji.vegan.backend.bean.dto.MenuCreate;
import ji.vegan.backend.bean.food.Menu;
import ji.vegan.backend.bean.food.QMenu;
import ji.vegan.backend.bean.food.Shop;
import ji.vegan.backend.bean.user.LoginUser;
import ji.vegan.backend.bean.user.UserInfo;
import ji.vegan.backend.service.FileUploadService;
import ji.vegan.backend.service.food.MenuService;
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
@RequestMapping("/api/menu")
@Slf4j
@RequiredArgsConstructor
public class MenuController {

    private static final String UPLOAD_PATH = "_menu";
    private final FileUploadService fileUploadService;
    private final MenuService menuService;
    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<Page<Menu>> lists(@QuerydslPredicate(root = Menu.class) Predicate predicate,
                                            @PageableDefault(sort = "id") Pageable pageable) {
        Page<Menu> page = menuService.findAll(predicate, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> detail(@PathVariable long id) {
        Menu menu = menuService.findById(id);
        return ResponseEntity.ok(menu);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Menu> insert(@LoginUser UserInfo userInfo,
                                       @RequestPart("data") MenuCreate menuCreate,
                                       @RequestPart(name = "image1", required = false) MultipartFile image1) {

        if (StringUtils.isEmpty(menuCreate.name())) {
            log.warn("menu name is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] menu name is empty error. ");
        }

        Shop shop = shopService.findById(menuCreate.shopId());

        /* 이름 존재 여부 체크 */
        if (menuService.exist(QMenu.menu.name.eq(menuCreate.name()).and(QMenu.menu.shop.id.eq(shop.getId())))) {
            log.warn("menu name is duplicate error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] menu name is duplicate error. ");
        }

        Menu menu = new Menu();
        menu.setShop(shop);
        menu.setName(menuCreate.name());
        menu.setPrice(menuCreate.price());
        menu.setDescription(menuCreate.description());
        menu.setCategories(menuCreate.categories());
        menu.setTags(menuCreate.tags());
        menu.setCreatedAt(ZonedDateTime.now());

        if (null != image1) {
            String image1String = fileUploadService.fileUpload(UPLOAD_PATH, image1);
            menu.setImage1(image1String);
        }

        Menu inserted = menuService.save(menu);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/menu/{id}")
                .buildAndExpand(menu.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(inserted);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Menu> modify(@LoginUser UserInfo userInfo,
                                       @PathVariable long id,
                                       @RequestPart("data") MenuCreate menuCreate,
                                       @RequestPart(name = "image1", required = false) MultipartFile image1) {
        if (StringUtils.isEmpty(menuCreate.name())) {
            log.warn("menu name is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] menu name is empty error. ");
        }

        Shop shop = shopService.findById(menuCreate.shopId());
        Menu menu = menuService.findById(id);

        if (null != image1) {
            if (StringUtils.isNotEmpty(menu.getImage1())) {
                fileUploadService.deleteFiles(UPLOAD_PATH, List.of(menu.getImage1()));
            }

            String image1String = fileUploadService.fileUpload(UPLOAD_PATH, image1);
            menu.setImage1(image1String);
        }

        menu.setShop(shop);
        menu.setName(menuCreate.name());
        menu.setPrice(menuCreate.price());
        menu.setDescription(menuCreate.description());
        menu.setCategories(menuCreate.categories());
        menu.setTags(menuCreate.tags());

        Menu updated = menuService.save(menu);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<Iterable<Menu>> delete(@LoginUser UserInfo userInfo,
                                                 @PathVariable List<Long> ids) {

        final Iterable<Menu> deleteList = menuService.findAll(QMenu.menu.id.in(ids));
        for (Menu menu : deleteList) {
            if (StringUtils.isNotEmpty(menu.getImage1())) {
                fileUploadService.deleteFiles(UPLOAD_PATH, List.of(menu.getImage1()));
            }
        }

        menuService.delete(deleteList);
        return ResponseEntity.ok(deleteList);
    }
}
