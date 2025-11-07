package net.happykoo.vcs.adapter.in.api;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.adapter.in.api.dto.CommentRequest;
import net.happykoo.vcs.adapter.in.api.dto.CommentResponse;
import net.happykoo.vcs.adapter.in.api.dto.Response;
import net.happykoo.vcs.adapter.in.resolver.LoginUser;
import net.happykoo.vcs.application.port.in.CommentUseCase;
import net.happykoo.vcs.domain.user.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentUseCase commentUseCase;

    @PostMapping
    public Response<String> createComment(@LoginUser User user, @RequestBody CommentRequest commentRequest) {
        var comment = commentUseCase.createComment(user, commentRequest);
        return Response.ok(comment.getId());
    }

    @PutMapping("{commentId}")
    public Response<String> updateComment(@LoginUser User user, @PathVariable String commentId, @RequestBody CommentRequest commentRequest) {
        var updateComment = commentUseCase.updateComment(commentId, user, commentRequest);
        return Response.ok(updateComment.getId());
    }

    @DeleteMapping("{commentId}")
    public Response<Void> deleteComment(@LoginUser User user, @PathVariable String commentId) {
        commentUseCase.deleteComment(commentId, user);
        return Response.ok();
    }

    @GetMapping(params = {"commentId"})
    public Response<CommentResponse> getComment(@RequestParam String commentId) {
        return Response.ok(commentUseCase.getComment(commentId));
    }

    @GetMapping(value = "list", params = {"videoId", "order", "offset", "maxSize"})
    public Response<List<CommentResponse>> listComments(
        @LoginUser User user,
        @RequestParam String videoId,
        @RequestParam(defaultValue = "TIME") String order,
        @RequestParam String offset,
        @RequestParam Integer maxSize
    ) {
        return Response.ok(commentUseCase.listComments(user, videoId, order, offset, maxSize));
    }

    @GetMapping(value = "reply", params = {"parentId", "offset", "maxSize"})
    public Response<List<CommentResponse>> listReplies(
        @RequestParam String parentId,
        @RequestParam String offset,
        @RequestParam Integer maxSize
    ) {
        return Response.ok(commentUseCase.listReplies(parentId, offset, maxSize));
    }

}
