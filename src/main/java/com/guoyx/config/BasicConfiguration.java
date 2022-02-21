package com.guoyx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class BasicConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        auth.inMemoryAuthentication()
                .withUser("mayan").password(encoder.encode("mayan@1210")).roles("USER")
                .and()
                .withUser("admin").password(encoder.encode("guoyx@0704")).roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //没有权限自动跳转到登陆页（自带登录页）
        http.formLogin();

        //请求授权规则
        http.authorizeRequests()
                .antMatchers("/download/**")
                .access("hasAnyRole('ADMIN','USER')")
                .antMatchers("/filePanel")
                .access("hasAnyRole('ADMIN','USER')");
    }

}
