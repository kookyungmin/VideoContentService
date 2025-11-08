package net.happykoo.vcs.domain.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewVideoMessage implements Serializable {
    private String channelId;
    private String videoId;
}
