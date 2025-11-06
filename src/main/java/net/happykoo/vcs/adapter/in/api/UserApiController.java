package net.happykoo.vcs.adapter.in.api;

import net.happykoo.vcs.adapter.in.api.dto.Response;
import net.happykoo.vcs.adapter.in.resolver.LoginUser;
import net.happykoo.vcs.domain.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {
    @GetMapping("login-user")
    public Response<User> getLoginUser(@LoginUser User user) {
        return Response.ok(user);
    }
}
