package org.richfaces.test.staging;

import static org.junit.Assert.*;

import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.junit.Test;
import org.richfaces.test.staging.RequestChain;
import org.richfaces.test.staging.ServerResource;
import org.richfaces.test.staging.ServletContainer;
import org.richfaces.test.staging.StaticServlet;
import org.richfaces.test.staging.StagingServletContext;

public class ServletTest {

	@Test
	public void testIsApplicable() {
		StaticServlet staticServlet = new StaticServlet();
		RequestChain servlet = new ServletContainer("/foo/*",staticServlet);
		assertTrue(servlet.isApplicable("/foo/bar.jsf"));
		assertFalse(servlet.isApplicable("/foz/bar.jsf"));
		assertFalse(servlet.isApplicable("bar"));
		servlet = new ServletContainer("*.jsf",staticServlet);
		assertTrue(servlet.isApplicable("/foo/bar.jsf"));
		assertFalse(servlet.isApplicable("bar"));
		try {
			servlet = new ServletContainer(".jsf",staticServlet);
		} catch (IllegalArgumentException e) {
			return;
		}
		assertFalse(true);
	}

	@Test
	public void testGetServletPath() {
		StaticServlet staticServlet = new StaticServlet();
		RequestChain servlet = new ServletContainer("/foo/*",staticServlet);
		assertEquals("/foo/", servlet.getServletPath("/foo/bar.jsf"));
		assertNull(servlet.getServletPath("/foz/bar.jsf"));
		servlet = new ServletContainer("*.jsf",staticServlet);
		assertEquals("/foo/bar.jsf", servlet.getServletPath("/foo/bar.jsf"));
	}

	@Test
	public void testGetPathInfo() {
		StaticServlet staticServlet = new StaticServlet();
		RequestChain servlet = new ServletContainer("/foo/*",staticServlet);
		assertEquals("bar.jsf", servlet.getPathInfo("/foo/bar.jsf"));
		assertNull(servlet.getPathInfo("/foz/bar.jsf"));
		servlet = new ServletContainer("*.jsf",staticServlet);
		assertNull(servlet.getPathInfo("/foo/bar.jsf"));

	}

	@Test
	public void testInit() throws ServletException {
		StaticServlet staticServlet = new StaticServlet();
		RequestChain servlet = new ServletContainer("/foo/*",staticServlet);
		StagingServletContext context = new StagingServletContext(){

			public String getMimeType(String file){
				return null;
			}

			@Override
			protected ServerResource getServerResource(String path) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void valueBound(ServletContextAttributeEvent event) {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void valueReplaced(ServletContextAttributeEvent event) {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void valueUnbound(
					ServletContextAttributeEvent servletContextAttributeEvent) {
				// TODO Auto-generated method stub
				
			}

			public int getEffectiveMajorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			public int getEffectiveMinorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			public Dynamic addServlet(String servletName, String className) {
				// TODO Auto-generated method stub
				return null;
			}

			public Dynamic addServlet(String servletName, Servlet servlet) {
				// TODO Auto-generated method stub
				return null;
			}

			public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
				// TODO Auto-generated method stub
				return null;
			}

			public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
				// TODO Auto-generated method stub
				return null;
			}

			public ServletRegistration getServletRegistration(String servletName) {
				// TODO Auto-generated method stub
				return null;
			}

			public Map<String, ? extends ServletRegistration> getServletRegistrations() {
				// TODO Auto-generated method stub
				return null;
			}

			public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
				// TODO Auto-generated method stub
				return null;
			}

			public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
				// TODO Auto-generated method stub
				return null;
			}

			public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName,
					Class<? extends Filter> filterClass) {
				// TODO Auto-generated method stub
				return null;
			}

			public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
				// TODO Auto-generated method stub
				return null;
			}

			public FilterRegistration getFilterRegistration(String filterName) {
				// TODO Auto-generated method stub
				return null;
			}

			public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
				// TODO Auto-generated method stub
				return null;
			}

			public SessionCookieConfig getSessionCookieConfig() {
				// TODO Auto-generated method stub
				return null;
			}

			public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
				// TODO Auto-generated method stub
				
			}

			public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
				// TODO Auto-generated method stub
				return null;
			}

			public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
				// TODO Auto-generated method stub
				return null;
			}

			public void addListener(String className) {
				// TODO Auto-generated method stub
				
			}

			public <T extends EventListener> void addListener(T t) {
				// TODO Auto-generated method stub
				
			}

			public void addListener(Class<? extends EventListener> listenerClass) {
				// TODO Auto-generated method stub
				
			}

			public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
				// TODO Auto-generated method stub
				return null;
			}

			public JspConfigDescriptor getJspConfigDescriptor() {
				// TODO Auto-generated method stub
				return null;
			}

			public ClassLoader getClassLoader() {
				// TODO Auto-generated method stub
				return null;
			}

			public void declareRoles(String... roleNames) {
				// TODO Auto-generated method stub
				
			}

			public String getVirtualServerName() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		servlet.init(context);
		assertSame(context,staticServlet.getServletContext());
	}

}
