package sun.net.www.protocol.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.ajax4jsf.resource.FacesResourceContext;
import org.ajax4jsf.resource.InternetResource;
import org.ajax4jsf.resource.ResourceBuilderImpl;
import org.ajax4jsf.resource.ResourceBuilderImpl.HeadersCantext;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.StringConverter;

public class ResourceURLConnection extends URLConnection {
    
    ResourceBuilderImpl builder;
    InternetResource resource;
    FacesResourceContext resourceContext;
    Headers headers;
    
    public ResourceURLConnection(URL url) {
        super(url);
        this.resourceContext = new FacesResourceContext(
                        FacesContext.getCurrentInstance());
        this.builder = (ResourceBuilderImpl)ResourceBuilderImpl.getInstance();
        this.resource = builder.createInternetResource(this.resourceContext, url.getFile());
        this.ifModifiedSince = resource.getLastModified(resourceContext).getTime();
    }

    private class Headers {
        private String[] h_keys;
        public Map<String, List<String>> headers;
        
        public Headers(HeadersCantext ctx) {
            resource.sendHeaders(ctx);
            this.headers = ctx.header_lists;
        }
        String get(String key) {
                return headers.containsKey(key) 
                    ? ""+headers.get(key).get(0)
                    : null;    
        }
        String get(int idx) {
            if (null == this.h_keys)
                this.h_keys = this.headers.keySet().toArray(
                        new String[this.headers.size()]);
            return this.get(h_keys[idx]);
        }
        String val(int idx) {
            return this.get(this.get(idx));
        }
    }
    private Headers headers() {
        if (null == headers)
            headers = new Headers(builder.new HeadersCantext(
                            resourceContext.getFacesContext()));
        return headers;
    }

    @Override
    public void connect() throws IOException {
        this.connected = true;
    }
    @Override
    public int getContentLength() {
        return this.resource.getContentLength(resourceContext);
    }
    @Override
    public long getContentLengthLong() {
        return this.resource.getContentLength(resourceContext);
    }
    @Override
    public String getContentType() {
        return this.resource.getContentType(resourceContext);
    }
    @Override
    public long getExpiration() {
        return this.resource.getExpired(resourceContext);
    }
    @Override
    public long getDate() {
        return this.resource.getLastModified(resourceContext).getTime();
    }
    @Override
    public String getHeaderField(String name) {
        return this.headers().get(name);
    }
    @Override
    public Map<String, List<String>> getHeaderFields() {
        return this.headers().headers;
    }
    @Override
    public int getHeaderFieldInt(String name, int Default) {
        return new StringConverter(Default)
                .convert(Integer.class,this.headers().get(name));
    }
    @Override
    public long getHeaderFieldLong(String name, long Default) {
        return new StringConverter(Default)
            .convert(Long.class, this.headers().get(name));
    }
    @Override
    public long getHeaderFieldDate(String name, long Default) {
        return new DateConverter(Default)
                .convert(Long.class, this.headers().get(name));
    }
    @Override
    public String getHeaderFieldKey(int n) {
        return this.headers().get(n);
    }
    @Override
    public String getHeaderField(int n) {
        return this.headers().val(n);
    }
    @Override
    public Object getContent() throws IOException {
        throw new UnknownServiceException();
    }
    @Override
    public Object getContent(Class[] classes) throws IOException {
        throw new UnknownServiceException();
    }
    @Override
    public InputStream getInputStream() throws IOException {
        return this.resource.getResourceAsStream(resourceContext);
    }
    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnknownServiceException();
    }

}
