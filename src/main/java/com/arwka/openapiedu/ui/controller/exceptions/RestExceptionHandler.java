package com.arwka.openapiedu.ui.controller.exceptions;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.NoSuchElementException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.zalando.problem.Problem;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = IllegalArgumentException.class)
  protected ResponseEntity<Object> illegalArgExceptionHandler() {
    System.out.println("illegalArg works");
    Problem problem = Problem.builder()
        .withTitle("Illegal Argument Exception")
        .withDetail("Unknown arguments handler works!")
        .withStatus(BAD_REQUEST)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
  }

  @ExceptionHandler(value = {NotFoundException.class, NoSuchElementException.class})
  protected ResponseEntity<Object> notFoundOrNoSuchElementExceptionHandler() {
    System.out.println("noSuchEl/NotFound works");
    Problem problem = Problem.builder()
        .withTitle("Not found or not such element exception")
        .withDetail("Not found & No such element handler works!")
        .withStatus(NOT_FOUND)
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
  }

  @ExceptionHandler(value = IllegalStateException.class)
  protected ResponseEntity<Object> illegalStateExceptionHandler() {
    System.out.println("illegalState works");
    Problem problem = Problem.builder()
        .withTitle("Illegal State Exception")
        .withDetail("Illegal State Exception handler works!")
        .withStatus(CONFLICT)
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
  }

  @ExceptionHandler(value = Exception.class)
  protected ResponseEntity<Object> otherExceptionsHandler() {
    System.out.println("otherExceptions works");
    Problem problem = Problem.builder()
        .withTitle("Internal server error")
        .withDetail("Internal server etc. error")
        .withStatus(INTERNAL_SERVER_ERROR)
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
  }

}
