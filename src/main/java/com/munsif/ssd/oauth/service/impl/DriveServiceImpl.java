package com.munsif.ssd.oauth.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.munsif.ssd.oauth.service.DriveService;

@Service
public class DriveServiceImpl implements DriveService {

	private Logger logger = LoggerFactory.getLogger(DriveServiceImpl.class);
	
	@Override
	public void uploadFile(String fileName) {
		logger.debug("INSIDE THE SERVICE... " + fileName);

	}

}
