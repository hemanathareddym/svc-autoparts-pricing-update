package com.autoparts.pricingupdate.handler;

import com.autoparts.pricingupdate.model.MicroserviceResponse;
import com.autoparts.pricingupdate.model.ora.PricingUpdate;
import com.autoparts.pricingupdate.model.pg.PricingMapping;
import com.autoparts.pricingupdate.repository.ora.PricingUpdateRepository;
import com.autoparts.pricingupdate.repository.pg.PricingMappingRepository;
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
import java.util.*;

import static com.autoparts.pricingupdate.constants.Constants.*;
import static com.autoparts.pricingupdate.util.ServiceUtils.appendCorrelationId;
import static java.util.Objects.isNull;

@Component
public class PricingAssignmentHandler {
	
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PricingUpdateRepository pricingUpdateRepository;

	@Autowired
	private PricingMappingRepository pricingMappingRepository;

	
	private Logger log = LoggerFactory.getLogger(PricingAssignmentHandler.class);

	public void notify(MicroserviceResponse msResponse) throws Exception{
		String source = msResponse.getRequestParam(SOURCE, DEFAULT);
		log.info("Pricing Assignment service notification received from "+source);
		List<PricingUpdate> pricingUpdateList = pricingUpdateRepository.findAll();
		log.info("Found "+pricingUpdateList.size() + " number of ct-pt changed records to process further ", appendCorrelationId(msResponse));
		List<PricingMapping> pricingMappingList = new ArrayList<>();
		pricingUpdateList.forEach(pricingUpdate -> {
			PricingMapping pricingMapping = new PricingMapping();
			pricingMapping.setCtId(pricingUpdate.getCtId());
			pricingMapping.setPtId(pricingUpdate.getPtId());
			pricingMapping.setCreatedTimestamp(new Date());
			pricingMappingList.add(pricingMapping);
		});
		log.info("Updating all records to Postgres DB", appendCorrelationId(msResponse));
		pricingMappingRepository.saveAll(pricingMappingList);
		log.info("Updated all records to Postgres DB", appendCorrelationId(msResponse));
		// TODO - Publish the records to Kafka
	}
	


}
