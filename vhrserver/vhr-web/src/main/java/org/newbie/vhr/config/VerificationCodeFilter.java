package org.newbie.vhr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.newbie.vhr.model.RespBean;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class VerificationCodeFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        /**
         * 如果是登陆请求，就拦截
         */
        if ("POST".equals(request.getMethod()) && "/doLogin".equals(request.getServletPath())) {
            // 登录请求
            String code = request.getParameter("code");
            String verify_code = (String) request.getSession().getAttribute("verify_code");
            if (code == null || verify_code ==null ||
                    "".equals(code) || !verify_code.toLowerCase().equals(code.toLowerCase())) {
                // 验证码不正确
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.write(new ObjectMapper().writeValueAsString(RespBean.error("验证码填写错误")));
                out.flush();
                out.close();
                return;
            }
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
