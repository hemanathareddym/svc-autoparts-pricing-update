package com.autoparts.pricingupdate.service;

import static com.autoparts.pricingupdate.constants.Constants.DATA;
import static com.autoparts.pricingupdate.constants.ErrorMessages.ERR_TIME_OUT;
import static com.autoparts.pricingupdate.util.ExceptionUtil.getFullStackTrace;
import static com.autoparts.pricingupdate.util.ExceptionUtil.handleException;
import static com.autoparts.pricingupdate.util.ServiceUtils.appendCorrelationId;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.autoparts.pricingupdate.base.*;
import com.autoparts.pricingupdate.constants.Constants;
import com.autoparts.pricingupdate.errors.MicroserviceException;
import com.autoparts.pricingupdate.handler.S3BucketHandler;
import com.autoparts.pricingupdate.model.MicroserviceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@MicroserviceController
@CrossOrigin
public class AutoPartsStagedAssignmentConsumer {
	
	@Autowired
    S3BucketHandler s3BucketHandler;
	
	private Logger log = LoggerFactory.getLogger(AutoPartsStagedAssignmentConsumer.class);

	@MicroserviceMethod
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    @MsLoggingContext({@Element(type = InputType.headers, inputPath = Constants.SHOP_GUID, loggingContextName = Constants.SHOP_GUID),
            @Element(type = InputType.headers, inputPath = Constants.CORRELATION_ID, loggingContextName = Constants.CORRELATION_ID)})
    public MicroserviceResponse upload(@RequestPart(value = "file") MultipartFile file, MicroserviceResponse msResponse) throws MicroserviceException {
		log.info("S3 Bucket Upload Request received : " + msResponse.toString() + appendCorrelationId(msResponse));
        Instant start = now();
        log.info("Microservice response is : " + msResponse);
        try {
//            validateMandatoryParams(msResponse, false);
            s3BucketHandler.upload(file, msResponse);
        } catch (MicroserviceException me) {
            //Don't update request failure here. It should be handled in the child method. If we update here, it will overwrite the status
            me.printStackTrace();
            log.error("An error occurred while processing the order " + getFullStackTrace(me));
            throw me;
        } catch (TimeoutException | InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            log.error("Timeout while calling downstream API  : (TimeoutException | InterruptedException | ExecutionException) :: " + getFullStackTrace(exception));
            handleException(exception, ERR_TIME_OUT + "Check Logs for stack trace", msResponse);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("An internal error occurred while processing the order : Check Logs for stacktrace :: orderId=" + getFullStackTrace(exception));
            handleException(exception, msResponse);
        }
        Instant end = now();
        log.info("S3 bucket image upload Response sent : " + msResponse.toString() + " :: in " + between(start, end).getSeconds() + " seconds " + appendCorrelationId(msResponse));
        
        return msResponse;
	}
	
	@MicroserviceMethod
    @RequestMapping(method = RequestMethod.POST, value = "/delete")
    @MsLoggingContext({@Element(type = InputType.headers, inputPath = Constants.SHOP_GUID, loggingContextName = Constants.SHOP_GUID),
            @Element(type = InputType.headers, inputPath = Constants.CORRELATION_ID, loggingContextName = Constants.CORRELATION_ID)})
    public MicroserviceResponse delete(MicroserviceResponse msResponse) throws MicroserviceException {
		log.info("S3 Bucket delete request received : " + msResponse.toString() + appendCorrelationId(msResponse));
        Instant start = now();
        log.info("Microservice response is : " + msResponse);
        try {
            validateMandatoryParams(msResponse, true);
            s3BucketHandler.delete(msResponse);
        } catch (MicroserviceException me) {
            //Don't update request failure here. It should be handled in the child method. If we update here, it will overwrite the status
            me.printStackTrace();
            log.error("An error occurred while processing the order " + getFullStackTrace(me));
            throw me;
        } catch (TimeoutException | InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            log.error("Timeout while calling downstream API  : (TimeoutException | InterruptedException | ExecutionException) :: " + getFullStackTrace(exception));
            handleException(exception, ERR_TIME_OUT + "Check Logs for stack trace", msResponse);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("An internal error occurred while processing the order : Check Logs for stacktrace :: orderId=" + getFullStackTrace(exception));
            handleException(exception, msResponse);
        }
        Instant end = now();
        log.info("S3 bucket delete Response sent : " + msResponse.toString() + " :: in " + between(start, end).getSeconds() + " seconds " + appendCorrelationId(msResponse));
        
        return msResponse;
	}

	private void validateMandatoryParams(MicroserviceResponse msResponse, boolean validate) throws MicroserviceException {
        if (validate && isNull(msResponse.getRequest())
                || isNull(msResponse.getRequest().get(DATA))) {
            throw new MicroserviceException(String.valueOf(BAD_REQUEST.value()), "Mandatory Request parameters like data missing in the request");
        }
    }
}
