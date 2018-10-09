package com.munsif.ssd.oauth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties
public class ApplicationConfig {

	@Value("${google.oauth.callback.uri}")
	private String CALLBACK_URI;

	@Value("${google.secret.key.path}")
	private Resource driveSecretKeys;

	@Value("${google.credentials.folder.path}")
	private Resource credentialsFolder;

	@Value("${myapp.temp.path}")
	private String temporaryFolder;

	public String getCALLBACK_URI() {
		return CALLBACK_URI;
	}

	public void setCALLBACK_URI(String CALLBACK_URI_p) {
		CALLBACK_URI = CALLBACK_URI_p;
	}

	public Resource getDriveSecretKeys() {
		return driveSecretKeys;
	}

	public void setDriveSecretKeys(Resource driveSecretKeys) {
		this.driveSecretKeys = driveSecretKeys;
	}

	public Resource getCredentialsFolder() {
		return credentialsFolder;
	}

	public void setCredentialsFolder(Resource credentialsFolder) {
		this.credentialsFolder = credentialsFolder;
	}

	public String getTemporaryFolder() {
		return temporaryFolder;
	}

	public void setTemporaryFolder(String temporaryFolder) {
		this.temporaryFolder = temporaryFolder;
	}

}
