package ru.singularity.evaluation360.exeptions;

public class DontFoundException extends ResourceNotFoundException {
    public DontFoundException(String message) {
        super(message);
    }
}
