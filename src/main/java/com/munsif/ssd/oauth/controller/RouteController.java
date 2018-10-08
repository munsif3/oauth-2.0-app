package com.munsif.ssd.oauth.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.munsif.ssd.oauth.service.AuthorizationService;

import com.munsif.ssd.oauth.util.ApplicationConfig;

@Controller
public class RouteController {

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
	private GoogleAuthorizationCodeFlow flow;

	@Autowired
	private ApplicationConfig config;

	@PostConstruct
	public void init() throws IOException {
		InputStreamReader reader = new InputStreamReader(config.getDriveSecretKeys().getInputStream());
		FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(config.getCredentialsFolder().getFile());

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(dataStoreFactory)
				.build();
	}

	@Autowired
	AuthorizationService authorizationService;

	@GetMapping("/")
	public String showHomePage() throws IOException {
		if (authorizationService.isUserAuthenticated(flow)) {
			return "home.html";
		} else {
			return "index.html";
		}
	}

	@GetMapping("/signin/google")
	public void doGoogleSignIn(HttpServletResponse response) throws IOException {
		response.sendRedirect(authorizationService.authenticateUserViaGoogle(flow));
	}

}
