/**
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.ajax4jsf.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ViewResource;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.ajax4jsf.Messages;
import org.ajax4jsf.resource.util.URLToStreamHelper;
import org.ajax4jsf.util.base64.Codec;
import org.ajax4jsf.webapp.WebXml;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Produce instances of InternetResource's for any types - jar resource, dynamic
 * created image, component-incapsulated etc. Realised as singleton class to
 * support cache, configuration etc.
 * 
 * @author shura (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.1 $ $Date: 2007/01/09 18:56:58 $
 * 
 */
public class ResourceBuilderImpl extends InternetResourceBuilder {

	private static final Log log = LogFactory.getLog(ResourceBuilderImpl.class);

	private static final String DATA_SEPARATOR = "/DATA/";
	private static final String DATA_BYTES_SEPARATOR = "/DATB/";

	private static final Pattern DATA_SEPARATOR_PATTERN = Pattern
			.compile("/DAT(A|B)/");


	private static final ResourceRenderer defaultRenderer = new MimeRenderer(null);
	private static final ResourceRenderer xhtmlRenderer = new MimeRenderer("application/xhtml+xml");
	private static final ResourceRenderer XMLRenderer = new MimeRenderer("text/xml");
	private static final ResourceRenderer swfRenderer = new MimeRenderer("application/x-shockwave-flash");
	private static final ResourceRenderer logfileRenderer = new LogfileRenderer();
	private static final ResourceRenderer scriptRenderer = new ScriptRenderer();
	private static final ResourceRenderer styleRenderer = new StyleRenderer();
	private static final ResourceRenderer jpegRenderer = new JpegRenderer();
	private static final ResourceRenderer pngRenderer = new PngRenderer();
	private static final ResourceRenderer gifRenderer = new GifRenderer();
	private static final ResourceRenderer hnmlRenderer = new HTMLRenderer();
	private static final ResourceRenderer templateCSSRenderer = new TemplateCSSRenderer();

	private static final ConcurrentHashMap<String, ResourceRenderer> renderers = new ConcurrentHashMap<String, ResourceRenderer>(10249);

	private static final ConcurrentHashMap<String, InternetResource> resources = new ConcurrentHashMap<String, InternetResource>(10240);
	
	static {
		renderers.put(".gif", gifRenderer);
		renderers.put(".jpeg", jpegRenderer);
		renderers.put(".jpg", jpegRenderer);
		renderers.put(".png", pngRenderer);
		renderers.put(".js", scriptRenderer);
		renderers.put(".css", templateCSSRenderer);
		renderers.put(".xcss", templateCSSRenderer);
		renderers.put(".log", logfileRenderer);
		renderers.put(".html", hnmlRenderer);
		renderers.put(".xhtml", xhtmlRenderer);
		renderers.put(".xml", XMLRenderer);
		renderers.put(".swf", swfRenderer);
	}
	
	/**
	 * keep resources instances . TODO - put this map to application-scope
	 * attribute, for support clastering environment.
	 */
	
	private long _startTime;

	private Codec codec;

	
	private static boolean beentheredonethat = false;
	
	static {
		// renderers.put(".htc",new BehaviorRenderer());
		// set in-memory caching ImageIO
		Thread thread = Thread.currentThread();
		ClassLoader initialTCCL = thread.getContextClassLoader();
		
		try {
			ClassLoader systemCL = ClassLoader.getSystemClassLoader();
			thread.setContextClassLoader(systemCL);
			ImageIO.setUseCache(false);
		} finally {
			thread.setContextClassLoader(initialTCCL);
		}

	}

	public WebXml getWebXml(FacesContext context) {
		WebXml webXml = WebXml.getInstance(context);
		if (null == webXml) {
			throw new FacesException(
new StringBuilder("Resources framework is not initialised, check web.xml for Filter configuration.\n\n")
.append("    <filter>\n") 
.append("        <filter-name>Ajax Filter</filter-name>\n") 
.append("        <filter-class>org.ajax4jsf.Filter</filter-class>\n") 
.append("    </filter>\n") 
.append("    <filter-mapping>\n") 
.append("        <filter-name>Ajax Filter</filter-name>\n") 
.append("        <servlet-name>Faces Servlet</servlet-name>\n") 
.append("        <dispatcher>FORWARD</dispatcher>\n") 
.append("        <dispatcher>REQUEST</dispatcher>\n") 
.append("        <dispatcher>INCLUDE</dispatcher>\n") 
.append("        <dispatcher>ERROR</dispatcher>\n")
.append("    </filter-mapping>\n\n")
.append("Filter configuration is missing.\n")
.toString());
		}
		return webXml;
	}
	
	public ResourceBuilderImpl(ResourceHandler res) {
		super(res);
		super.addInstance(this);
		log.info(">>>>>> RichfacesResourceHandler <<<<<<");
		_startTime = System.currentTimeMillis();
		codec = new Codec();
		init();
	}


	public ResourceBuilderImpl() {
		super();
		super.addInstance(this);
		log.info(">>>>>> new ResourceBuilder2.0...");
		_startTime = System.currentTimeMillis();
		codec = new Codec();
		super.addInstance(this);
	}

	/**
	 * @throws FacesException
	 */
	protected synchronized void registerResources() throws FacesException {
		if (beentheredonethat)
			return;
		beentheredonethat = true;
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> e = loader
					.getResources("META-INF/resources-config.xml");

			while (e.hasMoreElements()) {
				URL resource = e.nextElement();
				registerConfig(resource);
			}
			if (log.isDebugEnabled()) {
				log.debug("Found some resources:"+ resources.size());
				resources.keySet().forEach(k -> log.debug(k));
			}
		} catch (IOException e) {
			throw new FacesException(e);
		}
	}

	private synchronized void registerConfig(URL resourceConfig) {
		log.debug("Process cfg file: "+resourceConfig.getPath());
				
		try {
			if (log.isDebugEnabled()) {
				log.debug("Process resources configuration file "
						+ resourceConfig.toExternalForm());
			}

			InputStream in = URLToStreamHelper.urlToStream(resourceConfig);
			try {
				Digester digester = new Digester();
				digester.setValidating(false);
				digester.setEntityResolver(new EntityResolver() {
					// Dummi resolver - alvays do nothing
					public InputSource resolveEntity(String publicId,
							String systemId) throws SAXException, IOException {
						return new InputSource(new StringReader(""));
					}
				});
				digester.setNamespaceAware(false);
				digester.setUseContextClassLoader(true);
				digester.push(this);
				digester.addObjectCreate("resource-config/resource", "class",
						JarResource.class);
				digester.addObjectCreate("resource-config/resource/renderer",
						"class", HTMLRenderer.class);
				digester.addCallMethod(
						"resource-config/resource/renderer/content-type",
						"setContentType", 0);
				digester.addSetNext("resource-config/resource/renderer",
						"setRenderer", ResourceRenderer.class.getName());
				digester.addCallMethod("resource-config/resource/name",
						"setKey", 0);
				digester.addCallMethod("resource-config/resource/path",
						"setPath", 0);
				digester.addCallMethod("resource-config/resource/cacheable",
						"setCacheable", 0);
				digester.addCallMethod(
						"resource-config/resource/session-aware",
						"setSessionAware", 0);
				digester.addCallMethod("resource-config/resource/property",
						"setProperty", 2);
				digester.addCallParam("resource-config/resource/property/name",
						0);
				digester.addCallParam(
						"resource-config/resource/property/value", 1);
				digester.addCallMethod("resource-config/resource/content-type",
						"setContentType", 0);
				digester.addSetNext("resource-config/resource", "addResource",
						InternetResource.class.getName());
				digester.parse(in);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new FacesException(e);
		} catch (SAXException e) {
			throw new FacesException(e);
		}
	}

	/**
	 */
	public synchronized void init() throws FacesException {
		// TODO - mace codec configurable.
			 registerResources();	
	}

	/**
	 * Base point for creating resource. Must detect type and build appropriate
	 * instance. Currently - make static resource for ordinary request, or
	 * instance of class.
	 * 
	 * @param base
	 *            base object for resource ( resourcess in classpath will be get
	 *            relative to it package )
	 * @param path
	 *            key - path to resource, resource class name etc.
	 * @return
	 * @throws FacesException
	 */
	public InternetResource createInternetResource(Object base, String path)
			throws FacesException {
		// TODO - detect type of resource ( for example, resources location path
		// in Skin
		try {
			return getResource(path);
		} catch (ResourceNotFoundException e) {
			try {
				return getResource(buildKey(base, path));
			} catch (ResourceNotFoundException e1) {
				if (log.isDebugEnabled()) {
					log.debug(Messages.getMessage(Messages.BUILD_RESOURCE_INFO,
							path));
				}
			}
		}
		// path - is class name ?
		InternetResource res;
		try {
			Class<?> resourceClass = Class.forName(path);
			res = createDynamicResource(path, resourceClass);
		} catch (Exception e) {
			try {
				res = createJarResource(base, path);
			} catch (ResourceNotFoundException ex) {
				res = createStaticResource(path);
			}
			// TODO - if resource not found, create static ?
		}
		return res;
	}

	private String buildKey(Object base, String path) {
		if (path.startsWith("/")) {
			return path.substring(1);
		}
		if (null == base) {
			return path;
		}
		StringBuffer packageName = new StringBuffer(base.getClass()
				.getPackage().getName().replace('.', '/'));
		return packageName.append("/").append(path).toString();
	}

	public String getUri(InternetResource resource, FacesContext context,
			Object storeData) {
		StringBuffer uri = new StringBuffer();// ResourceServlet.DEFAULT_SERVLET_PATH).append("/");
		uri.append(resource.getKey());
		// append serialized data as Base-64 encoded request string.
		if (storeData != null) {
			try {
				byte[] objectData;
				if (storeData instanceof byte[]) {
					objectData = (byte[]) storeData;
					uri.append(DATA_BYTES_SEPARATOR);
				} else {
					ByteArrayOutputStream dataSteram = new ByteArrayOutputStream(
							1024);
					ObjectOutputStream objStream = new ObjectOutputStream(
							dataSteram);
					objStream.writeObject(storeData);
					objStream.flush();
					objStream.close();
					dataSteram.close();
					objectData = dataSteram.toByteArray();
					uri.append(DATA_SEPARATOR);
				}
				byte[] dataArray = encrypt(objectData);
				uri.append(new String(dataArray, "UTF-8"));

				// / byte[] objectData = dataSteram.toByteArray();
				// / uri.append("?").append(new
				// String(Base64.encodeBase64(objectData),
				// / "UTF-8"));
			} catch (Exception e) {
				// Ignore errors, log it
				log.error(Messages
						.getMessage(Messages.QUERY_STRING_BUILDING_ERROR), e);
			}
		}
		
		boolean isGlobal = !resource.isSessionAware();
		
		String resourceURL = getWebXml(context).getFacesResourceURL(context,
				uri.toString(), isGlobal);// context.getApplication().getViewHandler().getResourceURL(context,uri.toString());
		if (!isGlobal) {
			resourceURL = context.getExternalContext().encodeResourceURL(
					resourceURL);
		}
		if (log.isDebugEnabled()) {
			log.debug(Messages.getMessage(Messages.BUILD_RESOURCE_URI_INFO,
					resource.getKey(), resourceURL));
		}
		return resourceURL;// context.getExternalContext().encodeResourceURL(resourceURL);

	}

	/**
	 * @param key
	 * @return
	 */
	public InternetResource getResourceForKey(String key)
			throws ResourceNotFoundException {

		Matcher matcher = DATA_SEPARATOR_PATTERN.matcher(key);
		if (matcher.find()) {
			int data = matcher.start();
			key = key.substring(0, data);
		}

		return getResource(key);
	}

	public Object getResourceDataForKey(String key) {
		Object data = null;
		String dataString = null;
		Matcher matcher = DATA_SEPARATOR_PATTERN.matcher(key);
		if (matcher.find()) {
			if (log.isDebugEnabled()) {
				log.debug(Messages.getMessage(
						Messages.RESTORE_DATA_FROM_RESOURCE_URI_INFO, key,
						dataString));
			}
			int dataStart = matcher.end();
			dataString = key.substring(dataStart);
			byte[] objectArray = null;
			byte[] dataArray;
			try {
                 dataString = URLDecoder.decode(dataString, "UTF-8");
                 dataArray = dataString.getBytes("UTF-8");
                 objectArray = decrypt(dataArray);
			} catch (UnsupportedEncodingException e1) {
				// default encoding always presented.
			}
			if ("B".equals(matcher.group(1))) {
				data = objectArray;
			} else {
				try {
					ObjectInputStream in = new LookAheadObjectInputStream(new ByteArrayInputStream(objectArray));
					data = in.readObject();
				} catch (StreamCorruptedException e) {
					log.error(Messages.getMessage(Messages.STREAM_CORRUPTED_ERROR), e);
				} catch (IOException e) {
					log.error(Messages.getMessage(Messages.DESERIALIZE_DATA_INPUT_ERROR),e);
				} catch (ClassNotFoundException e) {
					log.error(Messages.getMessage(Messages.DATA_CLASS_NOT_FOUND_ERROR),e);
				}
			}
		}

		return data;
	}
	public InternetResource getResource(String path)
			throws ResourceNotFoundException {

		InternetResource internetResource = (InternetResource) resources.get(path);
		
		if (null == internetResource) {
			throw new ResourceNotFoundException("Resource not registered : "+ path);
		} else {
			return internetResource;
		}
	}

	public void addResource(InternetResource resource) {
	    resources.put(resource.getKey(), resource);
		ResourceRenderer renderer = resource.getRenderer(null);

        if (renderer == null) {
			setRenderer(resource, resource.getKey());
		}
	}

	public void addResource(String key, InternetResource resource) {
		resources.put(key, resource);
		resource.setKey(key);
		// TODO - set renderer ?
	}

	// public String getFacesResourceKey(HttpServletRequest request) {
	// return getWebXml(context).getFacesResourceKey(request);
	// }

	public String getFacesResourceURL(FacesContext context, String Url, boolean isGlobal) {
		return getWebXml(context).getFacesResourceURL(context, Url, isGlobal);
	}

	/**
	 * Build resource for link to static context in webapp.
	 * 
	 * @param path
	 * @return
	 * @throws FacesException
	 */
	protected InternetResource createStaticResource(String path)
			throws ResourceNotFoundException, FacesException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (null != context) {
			try {
				URL in = context.getExternalContext().getResource(path);
				if (null != in) {
					InternetResourceBase res = new StaticResource(path);
					setRenderer(res, path);
					// Detect last modification time.
					res.setLastModified(new Date(getStartTime()));
					addResource(path, res);
					return res;
				}
			} catch (MalformedURLException e) {
				throw new ResourceNotFoundException(Messages.getMessage(
						Messages.STATIC_RESOURCE_NOT_FOUND_ERROR, path),e);
			}
		}
		throw new ResourceNotFoundException(Messages.getMessage(
				Messages.STATIC_RESOURCE_NOT_FOUND_ERROR, path));
	}

	private void setRenderer(InternetResource res, String path)
			throws FacesException {
		int lastPoint = path.lastIndexOf('.');
		if (lastPoint > 0) {
			String ext = path.substring(lastPoint);
			ResourceRenderer resourceRenderer =  getRendererByExtension(ext);
			if (null != resourceRenderer) {
				res.setRenderer(resourceRenderer);
			} else {
				if (log.isDebugEnabled()) {
					log.debug(Messages.getMessage(
							Messages.NO_RESOURCE_REGISTERED_ERROR_2, path,
							renderers.keySet()));
				}

				// String mimeType = servletContext.getMimeType(path);
				res.setRenderer(defaultRenderer);
			}
		}
	}

	/**
	 * @param ext
	 * @return
	 */
	protected ResourceRenderer getRendererByExtension(String ext) {
		return renderers.get(ext);
	}

	/**
	 * Create resurce to send from classpath relative to base class.
	 * 
	 * @param base
	 * @param path
	 * @return
	 * @throws FacesException
	 */
	protected InternetResource createJarResource(Object base, String path)
			throws ResourceNotFoundException, FacesException {
		String key = buildKey(base, path);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (null != loader.getResource(key)) {
			JarResource res = new JarResource(key);
			setRenderer(res, path);
			res.setLastModified(new Date(getStartTime()));
			addResource(key, res);
			return res;
		} else {
			throw new ResourceNotFoundException(Messages.getMessage(
					Messages.NO_RESOURCE_EXISTS_ERROR, key));
		}

	}

	/**
	 * Create resource by instatiate given class.
	 * 
	 * @param path
	 * @param instatiate
	 * @return
	 */
	protected InternetResource createDynamicResource(String path,
			Class<?> instatiate) throws ResourceNotFoundException {
		if (InternetResource.class.isAssignableFrom(instatiate)) {
			InternetResource resource;
			try {
				resource = (InternetResource) instatiate.newInstance();
				addResource(path, resource);
			} catch (Exception e) {
				String message = Messages.getMessage(
						Messages.INSTANTIATE_RESOURCE_ERROR, instatiate
								.toString());
				log.error(message, e);
				throw new ResourceNotFoundException(message, e);
			}
			return resource;
		}
		throw new FacesException(Messages
				.getMessage(Messages.INSTANTIATE_CLASS_ERROR));
	}

	/**
	 * Create resource by instatiate {@link UserResource} class with given
	 * properties ( or got from cache ).
	 * 
	 * @param cacheable
	 * @param session
	 * @param mime
	 * @return
	 * @throws FacesException
	 */
	public InternetResource createUserResource(boolean cacheable,
			boolean session, String mime) throws FacesException {
		String path = getUserResourceKey(cacheable, session, mime);
		InternetResource userResource;
		try {
			userResource = getResource(path);
		} catch (ResourceNotFoundException e) {
			userResource = new UserResource(cacheable, session, mime);
			addResource(path, userResource);
		}
		return userResource;
	}

	/**
	 * Generate resource key for user-generated resource with given properties.
	 * 
	 * @param cacheable
	 * @param session
	 * @param mime
	 * @return
	 */
	private String getUserResourceKey(boolean cacheable, boolean session,
			String mime) {
		StringBuffer pathBuffer = new StringBuffer(UserResource.class.getName());
		pathBuffer.append(cacheable ? "/c" : "/n");
		pathBuffer.append(session ? "/s" : "/n");
		if (null != mime) {
			pathBuffer.append('/').append(mime.hashCode());
		}
		String path = pathBuffer.toString();
		return path;
	}

	/**
	 * @return Returns the startTime for application.
	 */
	public long getStartTime() {
		return _startTime;
	}

	protected byte[] encrypt(byte[] src) {
		try {
			Deflater compressor = new Deflater(Deflater.BEST_SPEED);
			byte[] compressed = new byte[src.length + 100];
			compressor.setInput(src);
			compressor.finish();
			int totalOut = compressor.deflate(compressed);
			byte[] zipsrc = new byte[totalOut];
			System.arraycopy(compressed, 0, zipsrc, 0, totalOut);
			compressor.end();
			return codec.encode(zipsrc);
		} catch (Exception e) {
			throw new FacesException("Error encode resource data", e);
		}
	}

	protected byte[] decrypt(byte[] src) {
		try {
			byte[] zipsrc = codec.decode(src);
			Inflater decompressor = new Inflater();
			byte[] uncompressed = new byte[zipsrc.length * 5];
			decompressor.setInput(zipsrc);
			int totalOut = decompressor.inflate(uncompressed);
			byte[] out = new byte[totalOut];
			System.arraycopy(uncompressed, 0, out, 0, totalOut);
			decompressor.end();
			return out;
		} catch (Exception e) {
			throw new FacesException("Error decode resource data", e);
		}
	}

	@Override
	public ResourceRenderer getScriptRenderer() {
		return scriptRenderer;
	}

	@Override
	public ResourceRenderer getStyleRenderer() {
		return styleRenderer;
	}
	
    // -------------------------------------------- Methods far ResourceHandler Implementation
    // Elaborate hack to reuse InternetResource sendHeaders function
	public class HeadersCantext extends FacesResourceContext {
		public Map<String, String> headers = new HashMap<String, String>();
		public Map<String, List<String>> header_lists = new HashMap<String, List<String>>();
		private List<String> headers_put(String key) {
		    if (!header_lists.containsKey(key))
		        header_lists.put(key, new ArrayList<String>());
		    return header_lists.get(key);
		}
		public void setContentType(String type) {
			headers.put("Content-Type", type);
			headers_put("Content-Type").add(type);
		}
		public void setContentLength(int contentLength) {
			headers.put("Content-Length", String.valueOf(contentLength));
			headers_put("Content-Length").add(String.valueOf(contentLength));
		}
		public void setDateHeader(String name, long value) {
            headers.put(name, String.valueOf(value));
            headers_put(name).add(String.valueOf(value));
		}
		public void setHeader(String name, String value) {
            headers.put(name, value);
            headers_put(name).add(value);
		}
		public void setIntHeader(String name, int value) {
            headers_put(name).add(String.valueOf(value));
		}
		public HeadersCantext(FacesContext facesContext) {
			super(facesContext);
		}
	}
	
	public class RichfacesResource extends Resource {
		
		private InternetResource resource;
		private FacesResourceContext context;
		private URL path;
		
		public RichfacesResource(InternetResource res) {
			this(FacesContext.getCurrentInstance(), res);
		}
		
		public RichfacesResource(FacesContext ctx, InternetResource res) {
			this.resource = res;
			this.context = new FacesResourceContext(ctx);
			try {
    			    this.path = new URL(InternetResource.RESOURCE_PROTOCOL, null,0, res.getKey()); 
            } catch (MalformedURLException e) {
                log.info("Swallow MalformedURLException:"+e.getMessage());
            }
			this.setContentType(res.getContentType(context));
			this.setLibraryName("richfaces");
			this.setResourceName(res.getKey());
			
		}
		
		@Override
		public InputStream getInputStream() throws IOException {
			return resource.getResourceAsStream(context);
		}


		@Override
		public String getRequestPath() {
			log.info("Providing path:"+this.path);
			return this.path.toString();
		}

		@Override
		public URL getURL() {
			return this.path;
		}

		@Override
		public Map<String, String> getResponseHeaders() {
			HeadersCantext ctx = new HeadersCantext(context.facesContext);
			resource.sendHeaders(ctx);
			return ctx.headers;
		}

		@Override
		public boolean userAgentNeedsUpdate(FacesContext ctx) {
			return System.currentTimeMillis() - resource.getLastModified(context).getTime() 
					< resource.getExpired(context);
		}
		
	}
	

    /**
     * <p class="changed_added_2_0"><span
     * class="changed_modified_2_2">Create</span> an instance of
     * <code>ViewResource</code> given the argument
     * <code>resourceName</code>.  The content-type of the resource is
     * derived by passing the <em>resourceName</em> to {@link
     * javax.faces.context.ExternalContext#getMimeType}</p>

     * <div class="changed_added_2_0">

     * <p>The algorithm specified in section JSF.2.6.1.4 of the spec
     * prose document <a
     * href="../../../overview-summary.html#prose_document">linked in
     * the overview summary</a> must be executed to create the
     * <code>Resource</code>.  <span class="changed_added_2_2">New
     * requirements were introduced in version 2.2 of the specification.
     * For historical reasons, this method operate correctly when the
     * argument {@code resourceName} is of the form
     * {@code libraryName/resourceName}, even when {@code resourceName}
     * contains '/' characters.  </span></p>

     * </div>

     * @param resourceName the name of the resource.
     *
     * @throws NullPointerException if <code>resourceName</code> is
     *  <code>null</code>.
     *
     * @return a newly created <code>Resource</code> instance, suitable
     * for use in encoding or decoding the named resource.
     */
    @Override
    public Resource createResource(String resourceName) {
	    	Resource res = null;
	    	try {
    			res = getWrapped().createResource(resourceName);
		} catch (Exception e) {
		    log.info("Normal create resource failed with swallawed exception: "
		                    +e.getMessage() + " Resource name: "+ resourceName);
			res = null;
		}
	    	try {
    		if (null == res)
    			return new RichfacesResource(createInternetResource(
    					FacesContext.getCurrentInstance().getExternalContext(), 
    					resourceName));
        } catch (Exception e) {
            log.info("Resource builder create resource failed with swallawed exception: "
                            +e.getMessage() + " Resource name: "+ resourceName);
            res = null;
        }
    			
    		return res;
    }
    /**
     * <p class="changed_added_2_0"><span
     * class="changed_modified_2_2">Create</span> an instance of
     * <code>Resource</code> with a resourceName given by the value of
     * the argument <code>resourceName</code> that is a member of the
     * library named by the argument <code>libraryName</code>.  The
     * content-type of the resource is derived by passing the
     * <em>resourceName</em> to {@link
     * javax.faces.context.ExternalContext#getMimeType}.</p>
     *
     * <div class="changed_added_2_0">

     * <p>The algorithm specified in section JSF.2.6.1.4 of the spec
     * prose document <a
     * href="../../../overview-summary.html#prose_document">linked in
     * the overview summary</a> must be executed to create the
     * <code>Resource</code>. <span class="changed_added_2_2">New
     * requirements were introduced in version 2.2 of the
     * specification.</span></p>

     * </div>

     * @param resourceName the name of the resource.
     *
     * @param libraryOrContractName <span class="changed_modified_2_2">the
     * name of the library (or contract) in which this resource
     * resides, may be <code>null</code>. If there is a conflict between
     * the name of a resource library and a resource library contract,
     * the resource library takes precedence.  <span
     * class="changed_modified_2_0_rev_a">May not include relative
     * paths, such as "../".</span></span>
     *
     * @throws <code>NullPointerException</code> if
     * <code>resourceName</code> is <code>null</code>
     *
     * @return a newly created <code>Resource</code> instance, suitable
     * for use in encoding or decoding the named resource.
     */
    @Override
    public Resource createResource(String resourceName, String libraryName) {
	    	Resource res = getWrapped().createResource(resourceName, libraryName);
//		log.info(resourceToString("createResource:"+libraryName+":"+resourceName+":", res));
	    	
	    	return res;
    }
    /**
     * <p class="changed_added_2_0"><span
     * class="changed_modified_2_2">Create</span> an instance of
     * <code>Resource</code> with a <em>resourceName</em> given by the
     * value of the argument <code>resourceName</code> that is a member
     * of the library named by the argument <code>libraryName</code>
     * that claims to have the content-type given by the argument
     * <code>content-type</code>.</p>
     *
     * <div class="changed_added_2_0">

     * <p>The algorithm specified in section JSF.2.6.1.4 of the spec
     * prose document <a
     * href="../../../overview-summary.html#prose_document">linked in
     * the overview summary</a> must be executed to create the
     * <code>Resource</code>. <span class="changed_added_2_2">New
     * requirements were introduced in version 2.2 of the
     * specification.</span></p>

     * </div>

     * @param resourceName the name of the resource.
     *
     * @param libraryName the name of the library in which this resource
     * resides, may be <code>null</code>.  <span
     * class="changed_modified_2_0_rev_a">May not include relative
     * paths, such as "../".</span>
     *
     * @param contentType the mime content that this
     * <code>Resource</code> instance will return from {@link
     * Resource#getContentType}.  If the value is <code>null</code>, The
     * content-type of the resource is derived by passing the
     * <em>resourceName</em> to {@link
     * javax.faces.context.ExternalContext#getMimeType}</p>
     *
     * @throws <code>NullPointerException</code> if
     * <code>resourceName</code> is <code>null</code>.
     *
     * @return a newly created <code>Resource</code> instance, suitable
     * for use in encoding or decoding the named resource.
     */
    @Override
    public Resource createResource(String resourceName, String libraryName, String contentType) {
    	    	Resource res = getWrapped().createResource(resourceName, libraryName, contentType);
	    	
	    	return res;
    }

    /**
     * <p class="changed_added_2_2">Create an instance of
     * <code>Resource</code> given the argument
     * <code>resourceId</code>.  The content-type of the resource is
     * derived by passing the <em>resourceName</em> to {@link
     * javax.faces.context.ExternalContext#getMimeType}</p>

     * <div class="changed_added_2_2">

     * <p>The resource must be identified according to the specification
     * in JSF.2.6.1.3 of the spec prose document <a
     * href="../../../overview-summary.html#prose_document">linked in
     * the overview summary</a>.  New requirements were introduced in
     * version 2.2 of the specification.</p>

     * </div>

     * @param resourceId the resource identifier of the resource.
     *
     * @throws NullPointerException if <code>resourceId</code> is
     *  <code>null</code>.
     *
     * @return a newly created <code>Resource</code> instance, suitable
     * for use in encoding or decoding the named resource.
     * 
     * @since 2.2
     */
    @Override
    public Resource createResourceFromId(String resourceId) {
		Resource res = getWrapped().createResourceFromId(resourceId);

		return res;

    }

    /**
     * <p class="changed_added_2_2">Create an instance of <code>Resource</code>
     * given the argument <code>resourceName</code>, which may contain "/" 
     * characters.  The {@link javax.faces.view.ViewDeclarationLanguage} calls
     * this method when it needs to load a view from a persistent store, such as
     * a filesystem.  This method is functionality equivalent to 
     * {@link #createResource(java.lang.String)}, but all callsites that need
     * to load VDL views must use this method so that classes that want to 
     * decorate the <code>ResourceHandler</code> in order to only affect the
     * loading of views may do so without affecting the processing of other
     * kinds of resources, such as scripts and stylesheets.
     * A {@link javax.faces.context.FacesContext} must be present
     * before calling this method.  To preserve compatibility with prior revisions of the
     * specification, a default implementation must be provided that calls
     * {@link #createResource(java.lang.String)}. </p>

     * <div class="changed_added_2_2">

     * <p>The default implementation must look for the resource in the
     * following places, in this order.</p>

     * <ul>

     * <li><p>Considering resource library contracts (at the locations
     * specified in the spec prose document section <em>Resource Library
     * Contracts</em> in the <em>Request Processing Lifecycle</em>
     * chapter).</p></li>

     * <li><p>Considering the web app root.</p></li>

     *  <li><p>Considering faces flows (at the locations specified in
     * the spec prose document section <em>Faces Flows</em> in the
     * <em>Using JSF in Web Applications</em> chapter).</p></li>

     * </ul>
     
     * <p>Call {@link FacesContext#getResourceLibraryContracts}.  If the
     * result is non-{@code null} and not empty, for each value in the
     * list, treat the value as the name of a resource library contract.
     * If the argument {@code resoureName} exists as a resource in the
     * resource library contract, return it.  Otherwise, return the
     * resource (not in the resource library contract), if found.
     * Otherwise, return {@code null}.</p>

     * </div>

     * @param context the {@link FacesContext} for this request.

     * @param resourceName the name of the resource to be interpreted as a view
     * by the {@link javax.faces.view.ViewDeclarationLanguage}.

     * @throws NullPointerException if <code>resourceName</code> is
     *  {@code null}.

     * @return a newly created {@link ViewResource} instance, suitable
     * for use by the {@link javax.faces.view.ViewDeclarationLanguage}.
     * 
     * @since 2.2

     */
    @Override
    public ViewResource createViewResource(FacesContext context, String resourceName) {
	    	return getWrapped().createViewResource(context, resourceName);
    }

    

    /**
     * <p class="changed_added_2_0">This method specifies the contract
     * for satisfying resource requests.  This method is called from
     * {@link javax.faces.webapp.FacesServlet#service} after that method
     * determines the current request is a resource request by calling
     * {@link #isResourceRequest}.  Thus, <code>handleResourceRequest</code>
     * may assume that the current request is a resource request.</p>
     *
     * <div class="changed_added_2_0">
     *
     * <p>The default implementation must implement an algorithm
     * semantically identical to the following algorithm.</p>
     *
     * For discussion, in all cases when a status code is to be set,
     * this spec talks only using the Servlet API, but it is understood
     * that in a portlet environment the appropriate equivalent API must
     * be used.
     *
     * <ul>
     *
     * <li><p>If the <em>resourceIdentifier</em> ends with any of the
     * extensions listed in the value of the {@link
     * #RESOURCE_EXCLUDES_PARAM_NAME} init parameter,
     * <code>HttpServletRequest.SC_NOT_FOUND</code> must be passed to
     * <code>HttpServletResponse.setStatus()</code>, then
     * <code>handleResourceRequest</code> must immediately return.</p></li>
     *
     * <li><p>Extract the <em>resourceName</em> from the
     * <em>resourceIdentifier</em> by taking the substring of
     * <em>resourceIdentifier</em> that starts at <code>{@link
     * #RESOURCE_IDENTIFIER}.length() + 1</code> and goes to the end of
     * <em>resourceIdentifier</em>.  If no <em>resourceName</em> can be
     * extracted, <code>HttpServletRequest.SC_NOT_FOUND</code> must be
     * passed to <code>HttpServletResponse.setStatus()</code>, then
     * <code>handleResourceRequest</code> must immediately return.</p></li>
     *
     * <li><p>Extract the <em>libraryName</em> from the request by
     * looking in the request parameter map for an entry under the key
     * "ln", without the quotes.  If found, use its value as the
     * <em>libraryName</em>.</p></li>
     *
     * <li><p>If <em>resourceName</em> and <em>libraryName</em> are
     * present, call {@link #createResource(String, String)} to create
     * the <code>Resource</code>.  If only <em>resourceName</em> is
     * present, call {@link #createResource(String)} to create the
     * <code>Resource</code>.  If the <code>Resource</code> cannot be
     * successfully created,
     * <code>HttpServletRequest.SC_NOT_FOUND</code> must be passed to
     * <code>HttpServletResponse.setStatus()</code>, then
     * <code>handleResourceRequest</code> must immediately return.</p></li>
     *
     * <li><p>Call {@link Resource#userAgentNeedsUpdate}.  If this
     * method returns false,
     * <code>HttpServletRequest.SC_NOT_MODIFIED</code> must be passed to
     * <code>HttpServletResponse.setStatus()</code>, then
     * <code>handleResourceRequest</code> must immediately return.</p></li>
     *
     * <li><p>Pass the result of {@link Resource#getContentType} to
     * <code>HttpServletResponse.setContentType.</code> </p></li>
     *
     * <li><p>Call {@link Resource#getResponseHeaders}.  For each entry
     * in this <code>Map</code>, call
     * <code>HttpServletResponse.setHeader()</code>, passing the key as
     * the first argument and the value as the second argument.</p></li>
     *
     * <li><p>Call {@link Resource#getInputStream} and serve up the
     * bytes of the resource to the response.</p></li>
     *
     * <li><p>Call <code>HttpServletResponse.setContentLength()</code>
     * passing the byte count of the resource.</p></li>
     *
     * <li><p>If an <code>IOException</code> is thrown during any of the
     * previous steps, log a descriptive, localized message, including
     * the <em>resourceName</em> and <em>libraryName</em> (if present).
     * Then, <code>HttpServletRequest.SC_NOT_FOUND</code> must be passed
     * to <code>HttpServletResponse.setStatus()</code>, then
     * <code>handleResourceRequest</code> must immediately return.</p></li>
     *
     * <li><p>In all cases in this method, any streams, channels,
     * sockets, or any other IO resources must be closed before this
     * method returns.</p></li>
     *
     * </ul>
     *
     * </div>
     *
     * @param context the {@link javax.faces.context.FacesContext} for this
     * request
     */
    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        getWrapped().handleResourceRequest(context);

    }


    /**
     * <p class="changed_added_2_0">Return <code>true</code> if the
     * current request is a resource request.  This method is called by
     * {@link javax.faces.webapp.FacesServlet#service} to determine if
     * this request is a <em>view request</em> or a <em>resource
     * request</em>.</p>
     *
     * @param context the {@link javax.faces.context.FacesContext} for this
     * request
     * @return <code>true</code> if the current request is a resource
     * request, <code>false</code> otherwise.
     */
    @Override
    public boolean isResourceRequest(FacesContext context) {
		return getWrapped().isResourceRequest(context);
    }

    /**
     * <p class="changed_added_2_2">Return {@code true} if the argument {@code url}
     * contains the string given by the value of the constant
     * {@link ResourceHandler#RESOURCE_IDENTIFIER}, false otherwise.</p>
     * 
     * @param url the url to inspect for the presence of {@link ResourceHandler#RESOURCE_IDENTIFIER}.

     * @throws NullPointerException if the argument url is {@code null}.
     */
    @Override
    public boolean isResourceURL(String url) {
	    	return getWrapped().isResourceURL(url);
    }
    
    /**
     * <p class="changed_added_2_0"><span
     * class="changed_modified_2_2">Return</span> <code>true</code> if
     * the resource library named by the argument
     * <code>libraryName</code> can be found.  <span
     * class="changed_added_2_2">If there is a <code>localePrefix</code>
     * for this application, as defined in {@link #LOCALE_PREFIX}, first
     * look for the library with the prefix.  If no such library is
     * found, look for the library without the prefix.  This allows
     * developers to avoid duplication of files.  For example, consider
     * the case where the developer wants to have a resource library
     * containing a localized image resource and a non-localized script
     * resource.  By checking both locations for the existence of the
     * library, along with other spec changes in section 2.6.1.4, this
     * scenario is enabled.</span></p>
     *
     * @since 2.0
     * 
     */
    @Override
    public boolean libraryExists(String libraryName) {
    		return getWrapped().libraryExists(libraryName);

    }
    
    /**
     * <p class="changed_added_2_0">Return the <code>renderer-type</code> for a 
     * {@link javax.faces.render.Renderer} that is capable of rendering this 
     * resource. The default implementation must return values according to the
     * following table.  If no <code>renderer-type</code> can be determined,
     * <code>null</code> must be returned.</p> 
     * 
     * <table border="1">
     * 
     * <tr>
     * 
     * <th>example resource name</th>
     * 
     * <th>renderer-type</th>
     * 
     * </tr>
     * 
     * <tr>
     * 
     * <td>mycomponent.js</td>
     * 
     * <td><code>javax.faces.resource.Script</code></td>
     * 
     * </tr>
     * 
     * <tr>
     * 
     * <td>mystyle.css</td>
     * 
     * <td><code>javax.faces.resource.Stylesheet</code></td>
     * 
     * </tr>
     * 
     * </table>
     */

    @Override
    public String getRendererTypeForResourceName(String resourceName) {
	    	return getWrapped().getRendererTypeForResourceName(resourceName);
    }

    public String resourceToString(String m, Resource res) {    		
		StringBuilder sb = new StringBuilder(m+" is:");
		if (null == res)
			sb.append("null");
		else {
    		sb.append(res.getLibraryName()+":"+res.getResourceName()+":"+res.getContentType());
    		sb.append("@"+res.getRequestPath()+" "+res.getURL().getPath());
		}
		return sb.toString();
    }
	
}
