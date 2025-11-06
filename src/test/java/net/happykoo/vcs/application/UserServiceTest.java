package net.happykoo.vcs.application;

import net.happykoo.vcs.application.port.out.LoadUserPort;
import net.happykoo.vcs.domain.user.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class UserServiceTest {
    private UserService userService;

    private final LoadUserPort loadUserPort = mock(LoadUserPort.class);

    @BeforeEach
    void setUp() {
        userService = new UserService(loadUserPort);
    }

    @Nested
    class GetUser {
        @Test
        @DisplayName("userId 가 null 이 아니면 해당되는 user 반환")
        void whenUserIdIsNotNullThenReturnUser() {
            var user = UserFixtures.stub();
            given(loadUserPort.loadUser(any())).willReturn(Optional.of(user));

            var result = userService.getUser("happykoo");

            then(result).isEqualTo(user);
        }

        @Test
        @DisplayName("userId 가 null 이면 null")
        void whenUserIdNotNullThenReturnNull() {
            var result = userService.getUser(null);
            then(result).isEqualTo(null);
        }
    }
}
