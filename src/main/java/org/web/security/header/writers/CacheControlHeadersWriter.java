package org.web.security.header.writers;
import org.web.security.header.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chen Hui
 * @since 2016/3/23
 */
public final class CacheControlHeadersWriter extends StaticHeadersWriter {

    /**
     * Creates a new instance
     */
    public CacheControlHeadersWriter() {
        super(createHeaders());
    }

    private static List<Header> createHeaders() {
        List<Header> headers = new ArrayList<Header>(2);
        headers.add(new Header("Cache-Control","no-cache, no-store, max-age=0, must-revalidate"));
        headers.add(new Header("Pragma","no-cache"));
        headers.add(new Header("Expires","0"));
        return headers;
    }
}
