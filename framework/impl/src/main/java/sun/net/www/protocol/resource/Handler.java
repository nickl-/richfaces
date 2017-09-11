package sun.net.www.protocol.resource;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        System.out.println("trying to open conn");
        return new sun.net.www.protocol.resource.ResourceURLConnection(u);
    }

}
