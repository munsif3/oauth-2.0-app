package com.munsif.ssd.oauth.service.impl;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.munsif.ssd.oauth.service.AuthorizationService;
import com.munsif.ssd.oauth.util.ApplicationConfig;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	private Logger logger = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

	private static final String USER_IDENTIFIER_KEY = "MY_TEST_USER";

	@Autowired
	private ApplicationConfig config;

	@Override
	public boolean isUserAuthenticated(GoogleAuthorizationCodeFlow flow) throws IOException {
		Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
		if (credential != null) {
			// talks to the google oauth server and asks for the access tokens and the
			// refresh token, if everything is fine, return true
			boolean isTokenValid = credential.refreshToken();
			logger.debug("isTokenValid, " + isTokenValid);
			return isTokenValid;
		}
		return false;
	}

	@Override
	public String authenticateUserViaGoogle(GoogleAuthorizationCodeFlow flow) throws IOException {
		GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		String redirectUrl = url.setRedirectUri(config.getCALLBACK_URI()).setAccessType("offline").build();
		logger.debug("redirectUrl, " + redirectUrl);
		return redirectUrl;
	}

	@Override
	public void exchangeCodeForTokens(GoogleAuthorizationCodeFlow flow, String code) throws IOException {
		//exchange the code against the access token and refresh token
		GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(config.getCALLBACK_URI()).execute();
		flow.createAndStoreCredential(tokenResponse, USER_IDENTIFIER_KEY);		
	}

}
