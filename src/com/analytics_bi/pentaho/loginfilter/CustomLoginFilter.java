package com.analytics_bi.pentaho.loginfilter;



import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
//import org.springframework.security.ui.AuthenticationEntryPoint;
import org.springframework.security.ui.WebAuthenticationDetails;
import org.springframework.util.Assert;

public class CustomLoginFilter implements Filter, InitializingBean {
	// ~ Static fields/initializers
	// =============================================

	private static final Log logger = LogFactory
			.getLog(CustomLoginFilter.class);
	

	// ~ Instance fields
	// ========================================================

	private AuthenticationManager authenticationManager;

	private String username;
	private String password;
	private String trusted_path;

	// ~ Methods
	// ================================================================

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.authenticationManager,
				"authenticationManager is required"); //$NON-NLS-1$
		Assert.notNull(this.username,
				"username parameter is required"); //$NON-NLS-1$

		Assert.notNull(this.password,
				"password parameter is required"); //$NON-NLS-1$

		Assert.notNull(this.trusted_path,
				"trusted_path parameter is required"); //$NON-NLS-1$
	}

	public void destroy() {
	}

	public void doFilter(final ServletRequest request,
			final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		Authentication existingAuth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (existingAuth == null){
			if (!(request instanceof HttpServletRequest)) {
				throw new ServletException(
						"RequestParameterAuthenticationFilter.ERROR_0005_HTTP_SERVLET_REQUEST_REQUIRED"); //$NON-NLS-1$
			}

			if (!(response instanceof HttpServletResponse)) {
				throw new ServletException(
						"RequestParameterAuthenticationFilter.ERROR_0006_HTTP_SERVLET_RESPONSE_REQUIRED"); //$NON-NLS-1$
			}

			HttpServletRequest httpRequest = (HttpServletRequest) request;
			boolean autoLogin = false;
			String currentURI = httpRequest.getRequestURI();
			String currentQueryString = httpRequest.getQueryString();
			
			logger.debug("currentURI: " + currentURI);
			logger.debug("currentQueryString: " + currentQueryString);
			
			
			if (currentURI.matches(trusted_path))
				autoLogin = true;			
			if ((currentQueryString!=null)  && (currentQueryString.matches(trusted_path)))
				autoLogin = true;
			
			logger.debug("autoLogin: " + String.valueOf(autoLogin));
			
			
			if (logger.isDebugEnabled()) {
				logger.debug("RequestParameterAuthenticationFilter.DEBUG_AUTH_USERID"); //$NON-NLS-1$
			}

			if (autoLogin ) {
				
				if ((existingAuth == null)
						|| !existingAuth.getName().equals(username)
						|| !existingAuth.isAuthenticated()) {
					UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
							username, password);
					authRequest.setDetails(new WebAuthenticationDetails(
							httpRequest));

					Authentication authResult;

					try {
						authResult = authenticationManager
								.authenticate(authRequest);
					} catch (AuthenticationException failed) {
						// Authentication failed
						if (logger.isDebugEnabled()) {
							logger.debug("RequestParameterAuthenticationFilter.DEBUG_AUTHENTICATION_REQUEST"); //$NON-NLS-1$
						}

						SecurityContextHolder.getContext().setAuthentication(
								null);

						chain.doFilter(request, response);

						return;
					}

					// Authentication success
					if (logger.isDebugEnabled()) {
						logger.debug("RequestParameterAuthenticationFilter.DEBUG_AUTH_SUCCESS"); //$NON-NLS-1$
					}

					SecurityContextHolder.getContext().setAuthentication(
							authResult);
				}
			}
		}
		chain.doFilter(request, response);
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void init(final FilterConfig arg0) throws ServletException {
	}

	public void setAuthenticationManager(
			final AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getTrusted_path() {
		return this.trusted_path;
	}

	public void setTrusted_path(final String trusted_path) {
		this.trusted_path = trusted_path;
	}
}
