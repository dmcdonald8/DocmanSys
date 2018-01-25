package main.config;

import main.controllers.CustomSuccessController;
import main.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

import static org.hibernate.criterion.Restrictions.and;

@Configuration
@EnableWebSecurity // Spring Security Defaults
/*
This makes sure anyone can access the index page and login page and also where permissions/restrictions
are set for certain roles
*/
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    CustomSuccessController customSuccessController;

    @Bean
    public UserDetailsService userDetailsServiceBean() {
        return new UserDetailsServiceImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {




        http.authorizeRequests()

                .antMatchers("/")
                .permitAll()
                .antMatchers("/upload")
                .permitAll()
                .antMatchers("/uploadRevision")
                .permitAll()
                .antMatchers("/uploadRevision/**")
                .permitAll()
                .antMatchers("/userDashboard")
                .access("hasRole('ROLE_ AUTHOR')")
                .antMatchers("/userDashboard")
                .access("hasRole('ROLE_DISTRIBUTEE')")
                .antMatchers("/login")
                .permitAll()
                .antMatchers("/admin/**")
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers("/adminDashboard")
                .access("hasRole('ROLE_ADMIN')")
                .and()

                //Configures form login
                .formLogin()

                .loginPage("/login").successHandler(customSuccessController)
                .permitAll()
                .and()
                //Configures the logout function
                .logout()
                .deleteCookies("JSESSIONID")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403");

    }

    @Bean
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint(){
        LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint("/login");
        return loginUrlAuthenticationEntryPoint;
    }

    // Using BCrypt to hash the password so that it's not viewed in plain text

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}