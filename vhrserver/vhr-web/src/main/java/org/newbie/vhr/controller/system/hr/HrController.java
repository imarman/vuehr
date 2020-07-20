package org.newbie.vhr.controller.system.hr;

import org.newbie.vhr.model.Hr;
import org.newbie.vhr.model.RespBean;
import org.newbie.vhr.model.Role;
import org.newbie.vhr.service.HrService;
import org.newbie.vhr.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/system/hr")
public class HrController {

    @Autowired
    private HrService hrService;

    @Autowired
    private RoleService roleService;

    /**
     * 操作员管理
     *  如果有 keyword， 关键字搜索
     *  没有就默认获取所有 hr
     */
    @GetMapping("/")
    public List<Hr> getAllHrs(String keywords) {
        return hrService.getAllHrs(keywords);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/")
    public RespBean updateHr(@RequestBody Hr hr) {
        if (hrService.updateHr(hr) == 1) {
            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }

    /**
     * 查询所有的角色
     */
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/role")
    public RespBean updateHrRole(Integer hrid, Integer[] rids) {
        if (hrService.updateHrRole(hrid, rids)) {
            return RespBean.ok("更新成功@");
        }
        return RespBean.error("更新失败!");
    }

    /**
     * 删除一用户 hr
     */
    @DeleteMapping("/{id}")
    public RespBean deleteHrById(@PathVariable Integer id) {
        if (hrService.deleteHrById(id) == 1) {
            return RespBean.ok("删除成功！");
        }
        return RespBean.error("删除失败");
    }
}
