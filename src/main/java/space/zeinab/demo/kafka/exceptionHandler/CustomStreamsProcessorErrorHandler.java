package space.zeinab.demo.kafka.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

@Slf4j
public class CustomStreamsProcessorErrorHandler implements StreamsUncaughtExceptionHandler {

    @Override
    public StreamThreadExceptionResponse handle(Throwable exception) {
        String invalidInputAndContinue = "userDataWithErrorAndContinue";
        String invalidInputAndShoutDown = "userDataWithErrorAndShoutDown";

        log.error("Exception in stream processing : {}  ", exception.getMessage(), exception);

        if (exception instanceof StreamsException) {
            String message = exception.getCause().getMessage();
            if (message.equals(invalidInputAndContinue)) {
                return StreamThreadExceptionResponse.REPLACE_THREAD;
            } else if (message.equals(invalidInputAndShoutDown)) {
                return StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
            }
        }
        return null;
    }
}

