package com.autoparts.pricingupdate.util;

import com.autoparts.pricingupdate.model.MicroserviceResponse;

import static com.autoparts.pricingupdate.constants.Constants.SPACE;

public class ServiceUtils {
	
	public static String appendCorrelationId(MicroserviceResponse response) {
        return " :: correlationId=" + response.getCorrelationId() + SPACE;
    }

}
