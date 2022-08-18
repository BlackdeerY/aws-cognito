package io.blackdeer.cognito.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Value("${spring.security.oauth2.client.registration.cognito.client-id}")
//    private String cognitoClientId;
//
//    @Value("${aws.cognito.logout-uri}")
//    private String cognitoLogoutUri;
//
//    @Value("${aws.cognito.logout-redirect-uri}")
//    private String cognitoLogoutRedirectUri;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        http.csrf()
                .and()
                .authorizeRequests()
                .antMatchers("/cognito/**").permitAll()
                .antMatchers("/**").authenticated()
//                .and()
//                .oauth2Login()
//                .and()
//                .logout()
//                .logoutSuccessUrl(String.format("%s?client_id=%s&logout_uri=%s", cognitoLogoutUri, cognitoClientId, cognitoLogoutRedirectUri))
                .and()
                .oauth2ResourceServer().jwt();
    }
}