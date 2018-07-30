package org.web.security.header.writers.frameoptions;

import org.springframework.util.Assert;

import java.util.regex.Pattern;

public final class RegExpAllowFromStrategy extends AbstractRequestParameterAllowFromStrategy {

    private final Pattern pattern;

    /**
     * Creates a new instance
     *
     * @param pattern
     *            the Pattern to compare against the HTTP parameter value. If
     *            the pattern matches, the domain will be allowed, else denied.
     */
    public RegExpAllowFromStrategy(String pattern) {
        Assert.hasText(pattern, "Pattern cannot be empty.");
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    protected boolean allowed(String allowFromOrigin) {
        return pattern.matcher(allowFromOrigin).matches();
    }
}
