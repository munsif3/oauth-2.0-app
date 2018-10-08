package com.munsif.ssd.oauth.service;

import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

public interface AuthorizationService {

	public boolean isUserAuthenticated(GoogleAuthorizationCodeFlow flow) throws IOException;

	public String authenticateUserViaGoogle(GoogleAuthorizationCodeFlow flow) throws IOException;

	public void exchangeCodeForTokens(GoogleAuthorizationCodeFlow flow, String code) throws IOException;
}
