package com.munsif.ssd.oauth.service.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.munsif.ssd.oauth.constant.ApplicationConstant;
import com.munsif.ssd.oauth.service.AuthorizationService;
import com.munsif.ssd.oauth.service.DriveService;
import com.munsif.ssd.oauth.util.ApplicationConfig;

@Service
public class DriveServiceImpl implements DriveService {

	private Logger logger = LoggerFactory.getLogger(DriveServiceImpl.class);

	private Drive driveService;

	@Autowired
	AuthorizationService authorizationService;

	@Autowired
	ApplicationConfig applicationConfig;

	@PostConstruct
	public void init() throws Exception {
		Credential credential = authorizationService.getCredentials();
		driveService = new Drive.Builder(ApplicationConstant.HTTP_TRANSPORT, ApplicationConstant.JSON_FACTORY, credential)
				.setApplicationName(ApplicationConstant.APPLICATION_NAME).build();
	}

	@Override
	public void uploadFile(MultipartFile multipartFile) throws Exception {
		logger.debug("Inside Upload Service...");

		String path = applicationConfig.getTemporaryFolder();
		String fileName = multipartFile.getOriginalFilename();
		String contentType = multipartFile.getContentType();

		java.io.File transferedFile = new java.io.File(path, fileName);
		multipartFile.transferTo(transferedFile);

		File fileMetadata = new File();
		fileMetadata.setName(fileName);
		
		FileContent mediaContent = new FileContent(contentType, transferedFile);
		File file = driveService.files().create(fileMetadata, mediaContent).setFields("id").execute();

		logger.debug("File ID: " + file.getName() + ", " + file.getId());
	}

}
