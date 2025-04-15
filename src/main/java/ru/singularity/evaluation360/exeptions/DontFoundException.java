package ru.singularity.evaluation360.exeptions;

public class DontFoundException extends RuntimeException {
    public DontFoundException(String message) {
        super(message);
    }
}
