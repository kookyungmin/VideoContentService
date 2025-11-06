package net.happykoo.vcs.application;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.application.port.in.UserUseCase;
import net.happykoo.vcs.application.port.out.LoadUserPort;
import net.happykoo.vcs.domain.user.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    private final LoadUserPort loadUserPort;

    @Override
    public User getUser(String userId) {
        if (userId == null) return null;
        return loadUserPort.loadUser(userId)
                .orElse(null);
    }
}
