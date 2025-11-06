package net.happykoo.vcs.adapter.out;

import net.happykoo.vcs.adapter.out.jpa.user.UserJpaEntity;
import net.happykoo.vcs.adapter.out.jpa.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class UserPersistenceAdapterTest {
    private UserPersistenceAdapter userPersistenceAdapter;

    private final UserJpaRepository userJpaRepository = mock(UserJpaRepository.class);

    @BeforeEach
    void setUp() {
        userPersistenceAdapter = new UserPersistenceAdapter(userJpaRepository);
    }

    @Test
    @DisplayName("loadUser 테스트")
    void test1() {
        // Given
        var userId = "happykoo";
        var userJpaEntity = new UserJpaEntity(userId, "해피쿠", "https://happykoo.net/profile.jpg");
        given(userJpaRepository.findById(any())).willReturn(Optional.of(userJpaEntity));

        // When
        var result = userPersistenceAdapter.loadUser(userId);

        // Then
        then(result)
            .isPresent()
            .hasValueSatisfying(user -> {
                then(user.getId()).isEqualTo(userId);
                then(user.getName()).isEqualTo("해피쿠");
            });
    }
}
