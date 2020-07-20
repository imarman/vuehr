package org.newbie.vhr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.newbie.vhr.model.Hr;
import org.newbie.vhr.model.RespBean;
import org.newbie.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    HrService hrService;

    @Autowired
    private CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;

    @Autowired
    private CustomUrlDecisionManager customUrlDecisionManager;

    @Autowired
    private VerificationCodeFilter verificationCodeFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 从数据库获取所有的管理者信息
     * @param auth 认证的信息
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 如果访问 /login 不用经过 Security 的拦截，直接访问
        web.ignoring().antMatchers("/login", "/css/**", "/js/**", "/index.html", "/img/**", "/fonts/**",
                "/favicon.ico",
                "/verifyCode", "/");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 把验证码的过滤器加在验证用户名和密码的过滤器前面
        http.addFilterBefore(verificationCodeFilter, UsernamePasswordAuthenticationFilter.class);
        // authorizeRequests() 方法来开始请求权限配置
        http.authorizeRequests()
                //.anyRequest().authenticated()  // 对 http 所有的请求必须通过授权认证才可以访问
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    // 根据角色 判断是否能访问
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setAccessDecisionManager(customUrlDecisionManager);
                        object.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
                        return object;
                    }
                })
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/doLogin")
                .loginPage("/login")
                /**
                 * 登陆成功返回的数据
                 * @HttpServletRequest 请求
                 * @HttpServletResponse 响应
                 * @Authentication 登陆成功的用户信息
                 */
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();  // response.getWriter()返回的是PrintWriter，这是一个打印输出流。
                        Hr hr = (Hr) authentication.getPrincipal();
                        hr.setPassword(null); // 不能传递密码
                        RespBean ok = RespBean.ok("登陆成功", hr);
                        String str = new ObjectMapper().writeValueAsString(ok); // 将对象转为json字符串
                        out.write(str);  // 返回到页面  response.getWriter().writer(),只能打印输出文本格式的（包括html标签），不可以打印对象。
                        out.flush();  // 将缓冲区的数据强制输出，用于清空缓冲区，若直接调用close()方法，则可能会丢失缓冲区的数据。
                        out.close();
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        RespBean error = RespBean.error("登陆失败");
                        if (e instanceof LockedException) {
                            error.setMsg("账户被锁定,请联系管理员!");
                        } else if (e instanceof CredentialsExpiredException) {
                            error.setMsg("密码过期,请联系管理员!");
                        } else if (e instanceof AccountExpiredException) {
                            error.setMsg("账户过期,请联系管理员!");
                        } else if (e instanceof DisabledException) {
                            error.setMsg("账户被锁定,请联系管理员!");
                        } else if (e instanceof BadCredentialsException) {
                            error.setMsg("用户名或者密码输入错误,请重新输入!");
                        }
                        out.write(new ObjectMapper().writeValueAsString(error));
                        out.flush();
                        out.close();
                    }
                })
                .permitAll()
                .and()
                .logout()
                /**
                 * 注销成功
                 */
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        RespBean logoutOk = RespBean.ok("注销成功!");
                        out.write(new ObjectMapper().writeValueAsString(logoutOk));
                        out.flush();
                        out.close();
                    }
                })
                .permitAll()
                .and()
                .csrf().disable()
                .exceptionHandling()
                // 权限不足（没有认证）时，在这里处理结果，不要重定向
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        response.setContentType("application/json;charset=utf-8");
                        response.setStatus(401); // 没有认证
                        PrintWriter out = response.getWriter();
                        RespBean error = RespBean.error("访问失败");
                        if (e instanceof InsufficientAuthenticationException) {
                            error.setMsg("请求失败,请联系管理员!");
                        }
                        out.write(new ObjectMapper().writeValueAsString(error));
                        out.flush();
                        out.close();
                    }
                });
    }
}
