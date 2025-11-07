package net.happykoo.vcs.adapter.in.api.dto;

public record CommentRequest(
   String videoId,
   String parentId,
   String text
) {
}
