package org.web.security.header.writers;

import org.web.security.header.HeaderWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @author Chen Hui
 * @since 2016/3/23
 */
public final class XXssProtectionHeaderWriter implements HeaderWriter {
    private static final String XSS_PROTECTION_HEADER = "X-XSS-Protection";

    private boolean enabled;

    private boolean block;

    private String headerValue;

    /**
     * Create a new instance
     */
    public XXssProtectionHeaderWriter() {
        this.enabled = true;
        this.block = true;
        updateHeaderValue();
    }

    public void writeHeaders(HttpServletRequest request,
                             HttpServletResponse response) {
        response.setHeader(XSS_PROTECTION_HEADER, headerValue);
    }

    /**
     * If true, will contain a value of 1. For example:
     *
     * <pre>
     * X-XSS-Protection: 1
     * </pre>
     *
     * or if {@link #setBlock(boolean)} is true
     *
     *
     * <pre>
     * X-XSS-Protection: 1; mode=block
     * </pre>
     *
     * If false, will explicitly disable specify that X-XSS-Protection is
     * disabled. For example:
     *
     * <pre>
     * X-XSS-Protection: 0
     * </pre>
     *
     * @param enabled the new value
     */
    public void setEnabled(boolean enabled) {
        if(!enabled) {
            setBlock(false);
        }
        this.enabled = enabled;
        updateHeaderValue();
    }


    /**
     * If false, will not specify the mode as blocked. In this instance, any
     * content will be attempted to be fixed. If true, the content will be
     * replaced with "#".
     *
     * @param enabled
     *            the new value
     */
    public void setBlock(boolean block) {
        if(!enabled && block) {
            throw new IllegalArgumentException("Cannot set block to true with enabled false");
        }
        this.block = block;
        updateHeaderValue();
    }

    private void updateHeaderValue() {
        if(!enabled) {
            this.headerValue = "0";
            return;
        }
        this.headerValue = "1";
        if(block) {
            this.headerValue += "; mode=block";
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + " [headerValue=" + headerValue + "]";
    }
}
