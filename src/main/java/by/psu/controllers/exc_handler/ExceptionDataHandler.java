package by.psu.controllers.exc_handler;

import by.psu.exceptions.BadRequestException;
import by.psu.exceptions.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
public class ExceptionDataHandler extends ExceptionAbstractHandler {

    private final Logger logger = Logger.getLogger(ExceptionDataHandler.class.getName());

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<ExceptionAbstractHandler.AwesomeException> handleEntityNotFoundException(
            EntityNotFoundException entityNotFoundException) {
        logger.severe(entityNotFoundException.getMessage());
        return new ResponseEntity<>(new AwesomeException(HttpStatus.NOT_FOUND, entityNotFoundException.getMessage()),
                HttpStatus.NOT_FOUND);
    }

/*
    @ExceptionHandler
    private ResponseEntity<ExceptionAbstractHandler.AwesomeException> handleEntityNotFoundException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(new AwesomeException(HttpStatus.CONFLICT, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
*/

    @ExceptionHandler
    private ResponseEntity<ExceptionAbstractHandler.MessageException> handleEntityNotFoundException(BadRequestException ex) {
        logger.severe(ex.getMessage());
        return new ResponseEntity<>(new MessageException(HttpStatus.BAD_REQUEST, ex.getReason(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
