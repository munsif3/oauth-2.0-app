package com.munsif.ssd.oauth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.munsif.ssd.oauth.model.UploadFile;
import com.munsif.ssd.oauth.service.AuthorizationService;
import com.munsif.ssd.oauth.service.DriveService;

@Controller
public class MainController {

	private Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	AuthorizationService authorizationService;

	@Autowired
	DriveService driveService;

	/**
	 * Handles the root request. Checks if user is already authenticated via SSO.
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/")
	public String showHomePage() throws Exception {
		if (authorizationService.isUserAuthenticated()) {
			logger.debug("User is authenticated. Redirecting to home...");
			return "redirect:/home";
		} else {
			logger.debug("User is not authenticated. Redirecting to sso...");
			return "redirect:/login";
		}
	}

	/**
	 * Directs to login
	 * 
	 * @return
	 */
	@GetMapping("/login")
	public String goToLogin() {
		return "index.html";
	}

	/**
	 * Directs to home
	 * 
	 * @return
	 */
	@GetMapping("/home")
	public String goToHome() {
		return "home.html";
	}

	/**
	 * Calls the Google OAuth service to authorize the app
	 * 
	 * @param response
	 * @throws Exception
	 */
	@GetMapping("/signin/google")
	public void doGoogleSignIn(HttpServletResponse response) throws Exception {
		logger.debug("SSO Called...");
		response.sendRedirect(authorizationService.authenticateUserViaGoogle());
	}

	/**
	 * Applications Callback URI for redirection from Google auth server after user
	 * approval/consent
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/oauth/callback")
	public String saveAuthorizationCode(HttpServletRequest request) throws Exception {
		logger.debug("SSO Callback invoked...");
		String code = request.getParameter("code");
		logger.debug("SSO Callback Code Value..., " + code);

		if (code != null) {
			authorizationService.exchangeCodeForTokens(code);
			return "home.html";
		}
		return "index.html";
	}

	/**
	 * Handles logout
	 * 
	 * @return
	 */
	@GetMapping("/logout")
	public String logout() {
		logger.debug("Logout invoked...");
		// authorizationService.removeUserSession();
		return "redirect:/login";
	}

	/**
	 * Handles the files being uploaded to GDrive
	 * 
	 * @param request
	 * @param uploadedFile
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/upload")
	public String uploadFile(HttpServletRequest request, @ModelAttribute UploadFile uploadedFile) throws Exception {
		MultipartFile multipartFile = uploadedFile.getMultipartFile();
		driveService.uploadFile(multipartFile);
		return "redirect:/home?status=success";
	}
}
