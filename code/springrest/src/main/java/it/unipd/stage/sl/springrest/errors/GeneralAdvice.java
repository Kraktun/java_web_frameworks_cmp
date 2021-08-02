package it.unipd.stage.sl.springrest.errors;

import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.lib.rsa.exceptions.UninitializedPrivateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class GeneralAdvice {

    @ResponseBody
    @ExceptionHandler(MessageTooLongException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    String messageTooLongException(MessageTooLongException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UninitializedPrivateKeyException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    String uninitializedPrivateKeyException(UninitializedPrivateKeyException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(EventIsUsedAsStarterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    String eventIsUsedAsStarterException(EventIsUsedAsStarterException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(CreationErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    String creationErrorException(CreationErrorException ex) {
        return ex.getMessage();
    }
}