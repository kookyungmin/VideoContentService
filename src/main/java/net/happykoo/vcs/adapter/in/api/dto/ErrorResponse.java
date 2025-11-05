package net.happykoo.vcs.adapter.in.api.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(String type, String detail, LocalDateTime timestamp) {
}
