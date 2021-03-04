package com.autoparts.pricingupdate.handler;

import com.autoparts.pricingupdate.model.MicroserviceResponse;
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

import static com.autoparts.pricingupdate.constants.Constants.DATA;
import static com.autoparts.pricingupdate.constants.Constants.FILE;
import static java.util.Objects.isNull;

@Component
public class S3BucketHandler {
	
	@Value("${spotten.aws.s3.public-access}")
	boolean publicAccess;

	@Value("${cloud.aws.region.static}")
	private String awsRegion;

	@Value("${spotten.aws.cloudfront.urlexpiry}")
	private long urlExpiry;

	@Value("${cloud.aws.cf.domain.name}")
	private String cloudfrontDomain;

	@Autowired
	private ObjectMapper mapper;

	
	private Logger log = LoggerFactory.getLogger(S3BucketHandler.class);

	public void upload(MultipartFile file, MicroserviceResponse msResponse) throws Exception{
		log.info("Upload service request received in handler");
	}
	
	public void delete(MicroserviceResponse msResponse) throws Exception {
		log.info("Delete service request received in handler");
		String file = msResponse.getRequest().get(DATA).get(FILE).toString();
		file = file.replaceAll("^\"|\"$", "");	//TODO: Getting file name with double quotes. Hence this hack to trim them. Need to find the reason. 
	}



	private Date getSignedUrlExpiration() {
		Date expiration = new Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += this.urlExpiry;
		expiration.setTime(expTimeMillis);
		return expiration;
	}

}
