package com.munsif.ssd.oauth.service;

public interface AuthorizationService {

	public boolean isUserAuthenticated() throws Exception;

	public String authenticateUserViaGoogle() throws Exception;

	public void exchangeCodeForTokens(String code) throws Exception;
}
