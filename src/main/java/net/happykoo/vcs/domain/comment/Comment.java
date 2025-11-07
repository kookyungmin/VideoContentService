package net.happykoo.vcs.domain.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class Comment {
    private String id;
    private String videoId;
    private String parentId;
    private String authorId;
    private String text;
    private LocalDateTime publishedAt;

    public void updateText(String text) {
        this.text = text;
    }
}
