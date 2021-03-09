package com.autoparts.pricingupdate.handler;

import com.autoparts.pricingupdate.model.MicroserviceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.autoparts.pricingupdate.constants.Constants.*;
import static java.util.Objects.isNull;

@Component
public class PricingAssignmentHandler {
	
	@Autowired
	private ObjectMapper mapper;

	
	private Logger log = LoggerFactory.getLogger(PricingAssignmentHandler.class);

	public void notify(MicroserviceResponse msResponse) throws Exception{
		String source = msResponse.getRequestParam(SOURCE, DEFAULT);
		log.info("Pricing Assignment service notification received from "+source);
	}
	


}
