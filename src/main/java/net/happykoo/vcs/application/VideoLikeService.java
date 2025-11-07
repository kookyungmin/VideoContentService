package net.happykoo.vcs.application;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.application.port.in.VideoLikeUseCase;
import net.happykoo.vcs.application.port.out.LikeVideoPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoLikeService implements VideoLikeUseCase {
    private final LikeVideoPort likeVideoPort;

    @Override
    public Long likeVideo(String videoId, String userId) {
        return likeVideoPort.addVideoLike(videoId, userId);
    }

    @Override
    public Long unlikeVideo(String videoId, String userId) {
        return likeVideoPort.removeVideoLike(videoId, userId);
    }

    @Override
    public Boolean isLikedVideo(String videoId, String userId) {
        return likeVideoPort.isVideoLikeMember(videoId, userId);
    }

    @Override
    public Long getVideoLikeCount(String videoId) {
        return likeVideoPort.getVideoLikeCount(videoId);
    }
}
