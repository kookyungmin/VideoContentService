package net.happykoo.vcs.domain.user;

public class UserFixtures {
    public static User stub() {
        return User.builder()
                .id("happykoo")
                .name("해피쿠")
                .profileImageUrl("https://happykoo.net/profile.jpg")
                .build();
    }

    public static User stub(String id) {
        return User.builder()
                .id(id)
                .name("name" + id)
                .profileImageUrl("https://happykoo.net/profile.jpg")
                .build();
    }
}
