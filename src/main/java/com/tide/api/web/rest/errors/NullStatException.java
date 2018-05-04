package com.tide.api.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class NullStatException extends AbstractThrowableProblem {

    public NullStatException() {
        super(ErrorConstants.FEEDBACK_RETURN_NULL, "Stat is null object", Status.INTERNAL_SERVER_ERROR);
    }
}
