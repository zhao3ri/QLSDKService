package com.qinglan.sdk.server.web.filter;

import com.qinglan.sdk.server.web.SessionIdHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class SessionFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);
    private String cookieSessionIdName;
    private String cookieDomain;
    private String cookiePath;

    public SessionFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.cookieSessionIdName = filterConfig.getInitParameter("cookieSessionIdName");
        this.cookieSessionIdName = this.cookieSessionIdName != null && !this.cookieSessionIdName.isEmpty() ? this.cookieSessionIdName : "sid";
        this.cookieDomain = filterConfig.getInitParameter("cookieDomain");
        this.cookieDomain = this.cookieDomain == null ? "" : this.cookieDomain;
        this.cookiePath = filterConfig.getInitParameter("cookiePath");
        this.cookiePath = this.cookiePath != null && !this.cookiePath.isEmpty() ? this.cookiePath : "/";
        logger.info("sessionFilter initial success");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        logger.debug("do filter url: {}", request.getRequestURI());
        String sid = this.findSessionId(request.getCookies());
        if (sid == null || sid.isEmpty()) {
            sid = UUID.randomUUID().toString();
            String[] domains = this.cookieDomain.split(";");
            if (domains != null && domains.length >= 0) {
                String[] var11 = domains;
                int var10 = domains.length;

                for (int var9 = 0; var9 < var10; ++var9) {
                    String domain = var11[var9];
                    Cookie cookie = new Cookie(this.cookieSessionIdName, sid);
                    cookie.setMaxAge(1296000);
                    cookie.setDomain(domain);
                    cookie.setPath(this.cookiePath);
                    response.addCookie(cookie);
                }
            }
        }

        SessionIdHolder.pushSessionId(sid);

        try {
            chain.doFilter(request, response);
        } finally {
            if (SessionIdHolder.getSessionId() != null) {
                SessionIdHolder.popSessionId();
            }

        }

    }

    private String findSessionId(Cookie[] cookies) {
        if (cookies != null && cookies.length > 0) {

            for (int i = 0; i < cookies.length; ++i) {
                Cookie cookie = cookies[i];
                if (cookie.getName().equals(this.cookieSessionIdName)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public void destroy() {
        logger.info("sessionFilter destroy");
    }
}
