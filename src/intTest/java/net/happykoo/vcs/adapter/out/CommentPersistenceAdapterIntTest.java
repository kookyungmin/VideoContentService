package net.happykoo.vcs.adapter.out;

import net.happykoo.vcs.adapter.out.mongo.comment.CommentDocument;
import net.happykoo.vcs.domain.comment.CommentFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
class CommentPersistenceAdapterIntTest {
    @Autowired
    private CommentPersistenceAdapter commentPersistenceAdapter;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("createComment 테스트")
    void test1() {
        var comment = CommentFixtures.stub(UUID.randomUUID().toString());

        commentPersistenceAdapter.saveComment(comment);

        then(mongoTemplate.findById(comment.getId(), CommentDocument.class))
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("text", comment.getText());
    }

    @Test
    @DisplayName("saveComment 테스트")
    void test2() {
        //given
        var comment = CommentFixtures.stub(UUID.randomUUID().toString());
        mongoTemplate.save(CommentDocument.from(comment));

        comment.updateText("new text");
        commentPersistenceAdapter.saveComment(comment);

        then(mongoTemplate.findById(comment.getId(), CommentDocument.class))
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("text", "new text");
    }

    @Test
    @DisplayName("deleteComment 테스트")
    void test3() {
        var comment = CommentFixtures.stub(UUID.randomUUID().toString());
        mongoTemplate.save(CommentDocument.from(comment));

        commentPersistenceAdapter.deleteComment(comment.getId());

        then(mongoTemplate.findById(comment.getId(), CommentDocument.class))
                .isNull();
    }

    @Test
    @DisplayName("loadComment 테스트")
    void test4() {
        var commentId = UUID.randomUUID().toString();
        var comment = CommentFixtures.stub(commentId);
        mongoTemplate.save(CommentDocument.from(comment));

        var result = commentPersistenceAdapter.loadComment(commentId);

        then(result)
            .isPresent()
            .hasValueSatisfying(c -> then(c.getId()).isEqualTo(comment.getId()));
    }
}
