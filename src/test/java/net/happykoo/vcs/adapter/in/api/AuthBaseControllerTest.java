package net.happykoo.vcs.adapter.in.api;

import net.happykoo.vcs.application.port.out.LoadUserPort;
import net.happykoo.vcs.application.port.out.UserSessionPort;
import net.happykoo.vcs.domain.user.User;
import net.happykoo.vcs.domain.user.UserFixtures;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class AuthBaseControllerTest {
    @MockBean
    private UserSessionPort userSessionPort;
    @MockBean
    private LoadUserPort loadUserPort;

    protected User user = UserFixtures.stub();
    protected String authKey = UUID.randomUUID().toString();

    void prepareUser() {
        given(userSessionPort.getUserId(anyString())).willReturn("happykoo");
        given(loadUserPort.loadUser(anyString())).willReturn(Optional.of(user));
    }
}
