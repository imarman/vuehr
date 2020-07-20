package org.newbie.vhr.controller.config;

import org.newbie.vhr.model.Menu;
import org.newbie.vhr.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 根据登陆用户的 id 动态获取菜单项
 */
@RestController
@RequestMapping("/system/config")
public class SystemConfigController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/menu")
    public List<Menu> getMenusById() {
        return menuService.getMenusByHrId();
    }

}
