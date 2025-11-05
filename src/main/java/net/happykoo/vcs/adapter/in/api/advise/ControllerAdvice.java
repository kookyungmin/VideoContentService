package net.happykoo.vcs.adapter.in.api.advise;

import net.happykoo.vcs.adapter.in.api.dto.ErrorResponse;
import net.happykoo.vcs.exception.BadRequestException;
import net.happykoo.vcs.exception.DomainNotFoundException;
import net.happykoo.vcs.exception.ForbiddenRequestException;
import net.happykoo.vcs.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadReqeust(Exception ex) {
        return ErrorResponse.builder()
                .type("badRequest")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorized(Exception ex) {
        return ErrorResponse.builder()
                .type("unauthorized")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ForbiddenRequestException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(Exception ex) {
        return ErrorResponse.builder()
                .type("forbidden")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DomainNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDomainNotFound(Exception ex) {
        return ErrorResponse.builder()
                .type("notFound")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnCaughtError(Exception ex) {
        return ErrorResponse.builder()
                .type("internalServerError")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
