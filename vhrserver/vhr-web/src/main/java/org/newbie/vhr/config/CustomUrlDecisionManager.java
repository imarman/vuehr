package org.newbie.vhr.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 判断 当前用户是否具备 MyFilter 分析出来的角色
 * 如果具备角色就通过， 不具备就拒绝
 */
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {
    /**
     * @param authentication 登陆成功之后用户的信息
     * @param o  请求对象
     * @param collection  MyFilter 里的 getAttributes 的返回值 （需要的角色）
     */
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        for (ConfigAttribute configAttribute : collection) {
            // 需要的角色
            String needRoles = configAttribute.getAttribute();
            // 登录之后就能访问
            if ("ROLE_LOGIN".equals(needRoles)) {
                // 在判断有没有登陆
                if (authentication instanceof AnonymousAuthenticationToken) {
                    // 没有登陆
                    throw new AccessDeniedException("尚未登陆，请登录！");
                }else {
                    return;
                }
            }
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();// 获取登陆用户的角色
            // 只需要判断  authorities 包不包含 collection 包含的任意一项
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(needRoles)) {
                    // 具备角色
                    return;
                }
            }
            // 不具备角色
            throw new AccessDeniedException("权限不足，请联系管理员！");
        }
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}