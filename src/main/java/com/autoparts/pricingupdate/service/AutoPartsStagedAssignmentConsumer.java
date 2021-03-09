package com.autoparts.pricingupdate.service;

import static com.autoparts.pricingupdate.constants.Constants.DATA;
import static com.autoparts.pricingupdate.constants.Constants.SOURCE;
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
import com.autoparts.pricingupdate.handler.PricingAssignmentHandler;
import com.autoparts.pricingupdate.model.MicroserviceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@MicroserviceController
@CrossOrigin
public class AutoPartsStagedAssignmentConsumer {
	
	@Autowired
    PricingAssignmentHandler pricingAssignmentHandler;
	
	private Logger log = LoggerFactory.getLogger(AutoPartsStagedAssignmentConsumer.class);

	@MicroserviceMethod
    @RequestMapping(method = RequestMethod.POST, value = "/notify")
    @MsLoggingContext({@Element(type = InputType.headers, inputPath = Constants.SHOP_GUID, loggingContextName = Constants.SHOP_GUID),
            @Element(type = InputType.headers, inputPath = Constants.CORRELATION_ID, loggingContextName = Constants.CORRELATION_ID)})
    public MicroserviceResponse notify(MicroserviceResponse msResponse) throws MicroserviceException {
		log.info("Pricing Assignment update request received : " + msResponse.toString() + appendCorrelationId(msResponse));
        Instant start = now();
        log.info("Microservice response is : " + msResponse);
        try {
            validateMandatoryParams(msResponse, false);
            pricingAssignmentHandler.notify(msResponse);
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
            log.error("An internal error occurred while serving the notification request : Check Logs for stacktrace :: " + getFullStackTrace(exception));
            handleException(exception, msResponse);
        }
        Instant end = now();
        log.info("Pricing Assignment update Response sent : " + msResponse.toString() + " :: in " + between(start, end).getSeconds() + " seconds " + appendCorrelationId(msResponse));
        
        return msResponse;
	}
	
	private void validateMandatoryParams(MicroserviceResponse msResponse, boolean validate) throws MicroserviceException {
        if (validate && isNull(msResponse.getRequestParam(SOURCE))) {
            throw new MicroserviceException(String.valueOf(BAD_REQUEST.value()), "Mandatory Request parameters like source is missing in the request");
        }
    }
}
