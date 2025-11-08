package net.happykoo.vcs.domain.comment;

import java.time.LocalDateTime;

public class CommentFixtures {
    public static Comment stub(String id) {
        return Comment.builder()
                .id(id)
                .videoId("videoId")
                .text("comment")
                .authorId("happykoo")
                .publishedAt(LocalDateTime.now())
                .build();
    }
}
