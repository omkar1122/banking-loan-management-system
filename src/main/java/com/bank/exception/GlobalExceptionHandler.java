package com.bank.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Maps exceptions to a user-friendly error page (view "error"). */
@ControllerAdvice(annotations = org.springframework.stereotype.Controller.class)
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    public ModelAndView notFound(Exception ex) {
        String msg = ex instanceof ResourceNotFoundException ? ex.getMessage() : "The page you requested does not exist.";
        return page(HttpStatus.NOT_FOUND, "Not found", msg);
    }

    @ExceptionHandler({BusinessRuleException.class, MethodArgumentTypeMismatchException.class})
    public ModelAndView badRequest(Exception ex) {
        String msg = ex instanceof BusinessRuleException ? ex.getMessage() : "Invalid value supplied in the request.";
        return page(HttpStatus.BAD_REQUEST, "Request could not be processed", msg);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView methodNotAllowed(Exception ex) {
        return page(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", "This URL does not support that kind of request.");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView generic(Exception ex) {
        log.error("Unhandled exception", ex);
        return page(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", "An unexpected error occurred. Please try again.");
    }

    private ModelAndView page(HttpStatus status, String title, String message) {
        ModelAndView mv = new ModelAndView("error");
        mv.setStatus(status);
        mv.addObject("errCode", status.value());
        mv.addObject("errTitle", title);
        mv.addObject("errMessage", message);
        return mv;
    }
}
