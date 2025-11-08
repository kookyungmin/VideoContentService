package net.happykoo.vcs.adapter.out.mongo.comment;

import net.happykoo.vcs.domain.comment.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
class CommentMongoRepositoryIntTest {
    @Autowired
    private CommentMongoRepository commentMongoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("publishedAt 기준으로 댓글 리스트 조회 테스트")
    void test1() {
        //given 댓글 10개 save
        var videoId = "videoId";
        for (int i = 0; i < 10; i++) {
            var id = UUID.randomUUID().toString();
            var comment = Comment.builder()
                        .id(id)
                        .videoId(videoId)
                        .text("text " + i)
                        .authorId("happykoo")
                        .publishedAt(LocalDateTime.now()).build();
            mongoTemplate.save(CommentDocument.from(comment));
        }

        var result = commentMongoRepository.findAllByVideoIdAndParentIdIsNullAndPublishedAtLessThanEqualOrderByPublishedAtDesc(
            videoId,
            LocalDateTime.now(),
            Limit.of(5)
        );

        then(result)
            .hasSize(5)
            .allMatch(document -> document.getParentId() == null)
            .extracting("publishedAt", LocalDateTime.class)
            .isSortedAccordingTo(Comparator.reverseOrder());
    }
}
