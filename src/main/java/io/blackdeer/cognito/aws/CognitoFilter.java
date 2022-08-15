package io.blackdeer.cognito.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CognitoFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(CognitoFilter.class);

    private final CognitoService cognitoService;

    @Autowired
    public CognitoFilter(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String accessToken = httpServletRequest.getHeader("Authorization");
        if (accessToken != null) {
            /*
                직접 JWT를 읽어서 Authentication을 형성하지 않고,
                aws와 통신하여 인증한다.
                직접 JWT를 읽어서 Authentication을 사용하는 security 예시는 다른 프로젝트에서...
             */
            CognitoUser cognitoUserOrNull = cognitoService.getUserOrNullByPersonalAccessToken(accessToken);
            if (cognitoUserOrNull != null) {
                request.setAttribute("cognitoUser", cognitoUserOrNull);
            }
        }
        chain.doFilter(request, response);
    }
}
