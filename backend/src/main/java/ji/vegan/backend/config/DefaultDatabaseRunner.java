package ji.vegan.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ji.vegan.backend.bean.food.Menu;
import ji.vegan.backend.bean.food.Review;
import ji.vegan.backend.bean.food.Shop;
import ji.vegan.backend.bean.user.Role;
import ji.vegan.backend.bean.user.User;
import ji.vegan.backend.service.FileUploadService;
import ji.vegan.backend.service.UserService;
import ji.vegan.backend.service.food.MenuService;
import ji.vegan.backend.service.food.ReviewService;
import ji.vegan.backend.service.food.ShopService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DefaultDatabaseRunner implements ApplicationRunner {
    private final UserService userService;
    private final ShopService shopService;
    private final MenuService menuService;
    private final ReviewService reviewService;

    private final FileUploadService fileUploadService;
    private final PasswordEncoder passwordEncoder;

    private final String ROOT_PATH = "data/";
    private final String SHOP_PATH = "shop/";
    private final String MENU_PATH = "menu/";
    private final String REVIEW_PATH = "review/";

    @Override
    public void run(ApplicationArguments args) {
        loadUser();
        loadShop(new ClassPathResource(ROOT_PATH + SHOP_PATH + "shop.csv"));
        loadMenu(new ClassPathResource(ROOT_PATH + MENU_PATH + "menu.csv"));
        loadReview(new ClassPathResource(ROOT_PATH + REVIEW_PATH + "review.csv"));
    }

    private void loadUser() {
        User admin = new User();
        admin.setUserId("admin");
        admin.setRole(Role.ADMIN);
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setFavoriteShop(Map.of());
        admin.setCreatedAt(ZonedDateTime.now());
        userService.save(admin);

        User user = new User();
        user.setUserId("user");
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFavoriteShop(Map.of());
        user.setCreatedAt(ZonedDateTime.now());
        userService.save(user);

        User test = new User();
        test.setUserId("test");
        test.setRole(Role.USER);
        test.setPassword(passwordEncoder.encode("1234"));
        test.setFavoriteShop(Map.of());
        test.setCreatedAt(ZonedDateTime.now());
        userService.save(test);
    }

    private void loadShop(ClassPathResource classPathResource) {
        try (CSVParser parser = readCsvString(classPathResource.getInputStream())) {
            for (CSVRecord record : parser.getRecords()) {

                List<String> data = record.toList();

                ClassPathResource classPathResource1 = new ClassPathResource(ROOT_PATH + SHOP_PATH + data.get(1));
                String image1 = fileUploadService.fileUpload("_shop", classPathResource1);

                ClassPathResource classPathResource2 = new ClassPathResource(ROOT_PATH + SHOP_PATH + data.get(2));
                String image2 = fileUploadService.fileUpload("_shop", classPathResource2);

                Shop shop = new Shop();
                shop.setName(data.get(0));
                shop.setImage1(image1);
                shop.setImage2(image2);
                shop.setRating(data.get(3));
                shop.setPhoneNumber(data.get(4).trim());
                shop.setAddress(data.get(5));
                shop.setLatitude(data.get(6));
                shop.setLongitude(data.get(7));
                shop.setTimes(Arrays.stream(StringUtils.split(data.get(8).trim(), "~")).toList());
                shop.setDescription(data.get(9));
                shop.setCreatedAt(ZonedDateTime.now());
                shop.setTags(data.get(10));

                shopService.save(shop);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadMenu(ClassPathResource classPathResource) {
        try (CSVParser parser = readCsvString(classPathResource.getInputStream())) {
            for (CSVRecord record : parser.getRecords()) {

                List<String> data = record.toList();

                ClassPathResource classPathResource1 = new ClassPathResource(ROOT_PATH + MENU_PATH + data.get(3));
                String image1 = fileUploadService.fileUpload("_menu", classPathResource1);

                Shop shop = shopService.findById(Integer.parseInt(data.get(0)));

                String[] split = StringUtils.split(data.get(4), '=');
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode objectNode = objectMapper.createObjectNode();
                for (String s : split) {
                    objectNode.put(s, true);
                }

                Menu menu = new Menu();
                menu.setShop(shop);
                menu.setName(data.get(1));
                menu.setPrice(Integer.parseInt(data.get(2)));
                menu.setImage1(image1);
                menu.setCategories(objectNode);
                menu.setDescription(data.get(5));
                menu.setTags(data.get(6));
                menu.setCreatedAt(ZonedDateTime.now());

                menuService.save(menu);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadReview(ClassPathResource classPathResource) {
        try (CSVParser parser = readCsvString(classPathResource.getInputStream())) {
            for (CSVRecord record : parser.getRecords()) {

                List<String> data = record.toList();

                ClassPathResource classPathResource1 = new ClassPathResource(ROOT_PATH + REVIEW_PATH + data.get(2));
                String image1 = fileUploadService.fileUpload("_review", classPathResource1);

                Shop shop = shopService.findById(Integer.parseInt(data.get(0)));
                User user = userService.findById(Integer.parseInt(data.get(6)));

                String[] advantages = StringUtils.split(data.get(4), '=');
                String[] disAdvantages = StringUtils.split(data.get(5), '=');

                Review review = new Review();
                review.setShop(shop);
                review.setContent(data.get(1));
                review.setRating(data.get(3));
                review.setImage1(image1);
                review.setAdvantages(Arrays.asList(advantages));
                review.setDisadvantage(Arrays.asList(disAdvantages));
                review.setUserId(user.getUserId());
                review.setCreatedAt(ZonedDateTime.now());

                reviewService.save(review);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private CSVParser readCsvString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return CSVFormat.Builder.create().setHeader().setIgnoreEmptyLines(false).setSkipHeaderRecord(true).build().parse(reader);
    }
}
