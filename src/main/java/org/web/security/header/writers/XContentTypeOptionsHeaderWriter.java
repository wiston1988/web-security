package org.web.security.header.writers;

/**
 * @author Chen Hui
 * @since 2016/3/23
 */
public final class XContentTypeOptionsHeaderWriter extends StaticHeadersWriter {

    /**
     * Creates a new instance
     */
    public XContentTypeOptionsHeaderWriter() {
        super("X-Content-Type-Options","nosniff");
    }
}