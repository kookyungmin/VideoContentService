package net.happykoo.vcs.adapter.in.api;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.adapter.in.api.dto.ChannelRequest;
import net.happykoo.vcs.adapter.in.api.dto.Response;
import net.happykoo.vcs.application.port.in.ChannelUseCase;
import net.happykoo.vcs.domain.channel.Channel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channels")
@RequiredArgsConstructor
public class ChannelApiController {
    private final ChannelUseCase channelUseCase;

    @PostMapping
    public Response<String> createChannel(@RequestBody ChannelRequest channelRequest) {
        var channel = channelUseCase.createChannel(channelRequest);

        return Response.ok(channel.getId());
    }

    @PutMapping("{channelId}")
    public Response<Void> updateChannel(@PathVariable String channelId, @RequestBody ChannelRequest channelRequest) {
        channelUseCase.updateChannel(channelId, channelRequest);

        return Response.ok();
    }

    @GetMapping("{channelId}")
    public Response<Channel> getChannel(@PathVariable String channelId) {
        Channel channel = channelUseCase.getChannel(channelId);
        return Response.ok(channel);
    }
}
