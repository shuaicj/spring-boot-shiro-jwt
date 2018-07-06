package shuaicj.example.shiro;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Error handling.
 *
 * @author shuaicj 2018/06/29
 */
@ControllerAdvice
public class AppControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(AppControllerAdvice.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handle(UnauthenticatedException e) {
        log.error("unauthorized", e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handle(AuthorizationException e) {
        log.error("forbidden", e);
    }
}