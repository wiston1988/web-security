package org.web.security.csrf;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Chen Hui
 * @since 2016/3/22
 */
public interface CsrfTokenRepository {

    /**
     * Generates a {@link CsrfToken}
     *
     * @param request
     *            the {@link HttpServletRequest} to use
     * @return the {@link CsrfToken} that was generated. Cannot be
     *         null.
     */
    CsrfToken generateToken(HttpServletRequest request);

    /**
     * Saves the {@link CsrfToken} using the {@link HttpServletRequest} and
     * {@link HttpServletResponse}. If the {@link CsrfToken} is null, it is the
     * same as deleting it.
     *
     * @param token
     *            the {@link CsrfToken} to save or null to delete
     * @param request
     *            the {@link HttpServletRequest} to use
     * @param response
     *            the {@link HttpServletResponse} to use
     */
    void saveToken(CsrfToken token, HttpServletRequest request,
                   HttpServletResponse response);

    /**
     * Loads the expected {@link CsrfToken} from the {@link HttpServletRequest}
     *
     * @param request
     *            the {@link HttpServletRequest} to use
     * @return the {@link CsrfToken} or null if none exists
     */
    CsrfToken loadToken(HttpServletRequest request);
}
