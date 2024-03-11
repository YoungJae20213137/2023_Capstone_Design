package ji.vegan.backend.service;

import com.querydsl.core.types.Predicate;
import ji.vegan.backend.bean.user.QUser;
import ji.vegan.backend.bean.user.User;
import ji.vegan.backend.bean.user.UserInfo;
import ji.vegan.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IService<User> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean matchPassword(String rawPassword, String encodePassword) {
        return passwordEncoder.matches(rawPassword, encodePassword);
    }

    @Transactional
    public void loginSuccess(UserInfo userInfo, User user) {
        userRepository.save(user);
    }

    public UserInfo getUserInfo(String userId) {
        Optional<User> optional = userRepository.findOne(QUser.user.userId.eq(userId));
        if (optional.isEmpty()) {
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] User not found");
        }

        User user = optional.get();
        return new UserInfo(user);
    }

    @Override
    public Page<User> findAll(Predicate predicate, Pageable pageable) {
        return userRepository.findAll(predicate, pageable);
    }

    @Override
    public Iterable<User> findAll(Predicate predicate) {
        return userRepository.findAll(predicate);
    }

    @Override
    public User findById(long id) {
        final Optional<User> byId = userRepository.findById(id);

        if (byId.isEmpty()) {
            log.error("user not exists. id : {}", id);
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] user not exists. id = " + id);
        }

        return byId.get();
    }

    @Override
    public Optional<User> findOne(Predicate predicate) {
        return userRepository.findOne(predicate);
    }

    @Override
    public boolean exist(Predicate predicate) {
        return userRepository.exists(predicate);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(Iterable<User> users) {
        userRepository.deleteAll(users);
    }

    @Override
    public long count(Predicate predicate) {
        return userRepository.count(predicate);
    }
}
