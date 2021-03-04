package com.autoparts.pricingupdate.errors;

import com.autoparts.pricingupdate.model.MicroserviceResponse;
import org.springframework.http.HttpStatus;

public class MicroserviceException extends Exception {
    private static final long serialVersionUID = -4768010568403992681L;
    private static final String VALIDATION_ERROR = "000000";
    private static final HttpStatus DEFAULT_HTTP_STATUS_CODE;
    private HttpStatus httpStatusCode;
    private String errorCode;
    private String errorMessage;
    private MicroserviceResponse msResponse;

    public MicroserviceException(String message) {
        super(message);
        this.httpStatusCode = DEFAULT_HTTP_STATUS_CODE;
        this.errorMessage = message;
        this.errorCode = "000000";
    }

    public MicroserviceException(String errorCode, String message) {
        super(message);
        this.httpStatusCode = DEFAULT_HTTP_STATUS_CODE;
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public MicroserviceException(HttpStatus httpStatusCode, String errorCode, String message) {
        this(errorCode, message);
        this.setHttpStatusCode(httpStatusCode);
    }

    public HttpStatus getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public void setHttpStatusCode(HttpStatus httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public MicroserviceException setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public MicroserviceResponse getMicroserviceResponse() {
        return this.msResponse;
    }

    public MicroserviceException setMicroserviceResponse(MicroserviceResponse msResponse) {
        this.msResponse = msResponse;
        return this;
    }

    public MicroserviceException setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    static {
        DEFAULT_HTTP_STATUS_CODE = HttpStatus.BAD_REQUEST;
    }
}
