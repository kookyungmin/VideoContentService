package net.happykoo.vcs.adapter.out.mongo.comment;

import org.springframework.data.domain.Limit;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentMongoRepository extends CrudRepository<CommentDocument, String> {
    List<CommentDocument> findAllByVideoIdAndParentIdIsNullAndPublishedAtLessThanEqualOrderByPublishedAtDesc(
        String videoId,
        LocalDateTime offset,
        Limit limit
    );

    List<CommentDocument> findAllByParentIdAndPublishedAtLessThanEqualOrderByPublishedAt(
        String parentId,
        LocalDateTime offset,
        Limit limit
    );
}
