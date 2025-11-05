package net.happykoo.vcs.adapter.in.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.happykoo.vcs.adapter.in.api.dto.ChannelRequest;
import net.happykoo.vcs.adapter.in.api.dto.ChannelSnippetRequest;
import net.happykoo.vcs.application.port.in.ChannelUseCase;
import net.happykoo.vcs.domain.channel.ChannelFixtures;
import net.happykoo.vcs.domain.channel.ChannelSnippet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelApiController.class)
public class ChannelApiControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChannelUseCase channelUseCase;

    @Captor
    ArgumentCaptor<ChannelRequest> channelRequestArgumentCaptor;

    @Test
    @DisplayName("POST /api/v1/channels 테스트")
    public void test1() throws Exception {
        var channelId = "happykoo";
        //given
        var channelRequest = new ChannelRequest(new ChannelSnippetRequest("happykoo-channel", "happykoo 채널", "https://happykoo.net/thumbnail.jpg"), "userId");
        given(channelUseCase.createChannel(any())).willReturn(ChannelFixtures.stub(channelId));

        //when
        mockMvc
            .perform(
                post("/api/v1/channels")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(channelRequest))
            )
            .andExpectAll(
                status().isOk(),
                jsonPath("$.data").value(channelId)
            );

        verify(channelUseCase).createChannel(channelRequestArgumentCaptor.capture());
        var argValue= channelRequestArgumentCaptor.getValue();
        assertThat(argValue.contentOwnerId()).isEqualTo(channelRequest.contentOwnerId());
        assertThat(argValue.snippet())
                .extracting(ChannelSnippetRequest::title, ChannelSnippetRequest::description, ChannelSnippetRequest::thumbnailUrl)
                .containsExactly(channelRequest.snippet().title(), channelRequest.snippet().description(), channelRequest.snippet().thumbnailUrl());
    }
}
