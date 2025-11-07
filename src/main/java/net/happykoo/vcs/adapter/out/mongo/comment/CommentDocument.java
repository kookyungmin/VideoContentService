package net.happykoo.vcs.adapter.out.mongo.comment;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.happykoo.vcs.domain.comment.Comment;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("vcs_document")
@AllArgsConstructor
@Getter
public class CommentDocument {
    @Id
    private String id;

    @Indexed
    private String videoId;

    @Indexed
    private String parentId;

    private String authorId;

    private String text;

    @Indexed
    private LocalDateTime publishedAt;

    public static CommentDocument from(Comment comment) {
        return new CommentDocument(
                comment.getId(),
                comment.getVideoId(),
                comment.getParentId(),
                comment.getAuthorId(),
                comment.getText(),
                comment.getPublishedAt()
        );
    }

    public Comment toDomain() {
        return Comment.builder()
                .id(this.getId())
                .videoId(this.getVideoId())
                .parentId(this.getParentId())
                .text(this.getText())
                .authorId(this.authorId)
                .publishedAt(this.getPublishedAt())
                .build();
    }
}
