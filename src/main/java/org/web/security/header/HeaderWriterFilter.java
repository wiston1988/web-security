
package org.web.security.header;

import org.web.security.SecurityFilter;
import org.web.security.data.FilterResultWrapper;
import org.web.security.header.writers.CacheControlHeadersWriter;
import org.web.security.header.writers.HstsHeaderWriter;
import org.web.security.header.writers.XContentTypeOptionsHeaderWriter;
import org.web.security.header.writers.XXssProtectionHeaderWriter;
import org.web.security.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.web.security.util.matcher.PathPatternsMatcher;
import org.web.security.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeaderWriterFilter implements SecurityFilter {

    /** Collection of {@link HeaderWriter} instances to  write out the headers to the response . */
    private final List<HeaderWriter> headerWriters = new ArrayList<HeaderWriter>();
    private RequestMatcher ignoreXFrameOptionsHeaderPatterns = new PathPatternsMatcher();
    private RequestMatcher ignoreFilterPatterns = new PathPatternsMatcher();
    public HeaderWriterFilter(){
        addDefaultHeaderWriters();
    }
    @Override
    public FilterResultWrapper doFilterInvoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if(ignoreFilterPatterns.matches(request)){
            return new FilterResultWrapper(request,response);
        }
        for (HeaderWriter factory : headerWriters) {
            if(ignoreXFrameOptionsHeaderPatterns.matches(request)
                    && factory instanceof XFrameOptionsHeaderWriter){
                continue;
            }
            factory.writeHeaders(request, response);
        }
        return new FilterResultWrapper(request,response);
    }
    private void addDefaultHeaderWriters() {
        addHeaderWriter(new XContentTypeOptionsHeaderWriter());
        addHeaderWriter(new XXssProtectionHeaderWriter());
        addHeaderWriter(new CacheControlHeadersWriter());
        addHeaderWriter(new HstsHeaderWriter());
        addHeaderWriter(new XFrameOptionsHeaderWriter());
    }
    public void addHeaderWriter(HeaderWriter headerWriter) {
        Assert.notNull(headerWriter, "headerWriter cannot be null");
        this.headerWriters.add(headerWriter);
    }
    public void setIgnoreFilterPatterns(String ignoreFilterPatterns) {
        Assert.notNull(ignoreFilterPatterns, "ignoreFilterUriPatterns cannot be null");
        this.ignoreFilterPatterns = new PathPatternsMatcher(StringUtils.commaDelimitedListToStringArray(ignoreFilterPatterns));
    }
    public void setIgnoreXFrameOptionsHeaderPatterns(String ignoreXFrameOptionsHeaderPatterns) {
        Assert.notNull(ignoreXFrameOptionsHeaderPatterns, "ignoreXFrameOptionsHeaderPatterns cannot be null");
        this.ignoreXFrameOptionsHeaderPatterns = new PathPatternsMatcher(StringUtils.commaDelimitedListToStringArray(ignoreXFrameOptionsHeaderPatterns));
    }
}
