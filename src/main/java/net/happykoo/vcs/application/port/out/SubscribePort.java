package net.happykoo.vcs.application.port.out;

import net.happykoo.vcs.domain.channel.Channel;
import net.happykoo.vcs.domain.user.User;

import java.util.List;

public interface SubscribePort {
    String insertSubscribeChannel(Channel channel, User user);

    void deleteSubscribeChannel(Channel channel, User user);

    List<Channel> listSubscribeChannel(String userId);
    List<User> findAllSubscriber(String channelId);
}
