package net.happykoo.vcs.adapter.in.resolver;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.adapter.in.api.attribute.HeaderAttribute;
import net.happykoo.vcs.application.port.out.LoadUserPort;
import net.happykoo.vcs.application.port.out.UserSessionPort;
import net.happykoo.vcs.domain.user.User;
import net.happykoo.vcs.exception.DomainNotFoundException;
import net.happykoo.vcs.exception.UnauthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@Component
public class UserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserSessionPort userSessionPort;
    private final LoadUserPort loadUserPort;

    public UserHandlerMethodArgumentResolver(@Nullable UserSessionPort userSessionPort,
                                             @Nullable LoadUserPort loadUserPort) {
        this.userSessionPort = userSessionPort;
        this.loadUserPort = loadUserPort;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        var authKey = webRequest.getHeader(HeaderAttribute.X_AUTH_KEY);
        if (authKey == null) {
            return null;
        }

        var userId = userSessionPort.getUserId(authKey);
        if (userId == null) {
            throw new UnauthorizedException("Authkey is wrong");
        }

        return loadUserPort.loadUser(userId)
                .orElseThrow(DomainNotFoundException::new);
    }
}
