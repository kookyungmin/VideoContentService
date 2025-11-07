package net.happykoo.vcs.application.port.out;

public interface LikeCommentPort {
    Long getCommentLikeCount(String commentId);
    //TODO: like 하는 로직 추가해야함
}
