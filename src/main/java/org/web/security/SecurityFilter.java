package org.web.security;

import org.web.security.data.FilterResultWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/*
 * @author Chen Hui
 */
public interface SecurityFilter {

	FilterResultWrapper doFilterInvoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
