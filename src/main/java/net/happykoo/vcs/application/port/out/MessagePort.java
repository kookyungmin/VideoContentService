package net.happykoo.vcs.application.port.out;

public interface MessagePort {
    void sendNewVideMessage(String channelId, String videoId);
}
