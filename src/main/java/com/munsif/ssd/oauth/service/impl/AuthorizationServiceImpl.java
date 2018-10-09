package com.munsif.ssd.oauth.service.impl;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.munsif.ssd.oauth.service.AuthorizationService;
import com.munsif.ssd.oauth.util.ApplicationConfig;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	private Logger logger = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
	private static final String USER_IDENTIFIER_KEY = "MY_TEST_USER";

	private GoogleAuthorizationCodeFlow flow;

	@Autowired
	private ApplicationConfig config;

	@PostConstruct
	public void init() throws Exception {
		InputStreamReader reader = new InputStreamReader(config.getDriveSecretKeys().getInputStream());
		FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(config.getCredentialsFolder().getFile());

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(dataStoreFactory)
				.build();
	}

	@Override
	public boolean isUserAuthenticated() throws Exception {
		Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
		if (credential != null) {
			boolean isTokenValid = credential.refreshToken();
			logger.debug("isTokenValid, " + isTokenValid);
			return isTokenValid;
		}
		return false;
	}

	@Override
	public String authenticateUserViaGoogle() throws Exception {
		GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		String redirectUrl = url.setRedirectUri(config.getCALLBACK_URI()).setAccessType("offline").build();
		logger.debug("redirectUrl, " + redirectUrl);
		return redirectUrl;
	}

	@Override
	public void exchangeCodeForTokens(String code) throws Exception {
		// exchange the code against the access token and refresh token
		GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(config.getCALLBACK_URI()).execute();
		flow.createAndStoreCredential(tokenResponse, USER_IDENTIFIER_KEY);
	}

}
