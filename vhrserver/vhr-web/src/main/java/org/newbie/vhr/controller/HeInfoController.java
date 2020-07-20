package org.newbie.vhr.controller;

import org.newbie.vhr.model.Hr;
import org.newbie.vhr.model.RespBean;
import org.newbie.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HeInfoController {

    @Autowired
    private HrService hrService;

    @GetMapping("/hr/info")
    public Hr getCurrentHr(Authentication authentication){
        return (Hr) authentication.getPrincipal();
    }

    @PutMapping("/hr/info")
    public RespBean updateHr(@RequestBody Hr hr,Authentication authentication) {
        if (hrService.updateHr(hr) == 1) {
            // 更改用户信息，重新构建 Authentication 示例，放到 Context 中去
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(hr,
                    authentication.getCredentials(), authentication.getAuthorities()));

            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }

    @PutMapping("/hr/pass")
    public RespBean updateHrPassword(@RequestBody Map<String, Object> info) {
        String oldpass = (String) info.get("oldPass");
        String pass = (String) info.get("pass");
        Integer hrid = (Integer) info.get("hrid");
        if (hrService.updateHrPassword(oldpass, pass, hrid)) {
            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }
}
