package org.newbie.vhr.service;


import org.newbie.vhr.model.Role;
import org.newbie.vhr.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    public List<Role> getAllRoles() {
        return roleMapper.getAllRoles();
    }

    /**
     * 添加 角色
     * 因为素有角色名的前缀都是 'ROLE_' 所以要判断一下是否有 ROLE_ 前缀
     * 如果没有就自己加上
     * 如果有直接添加
     */
    public Integer addRole(Role role) {
        if (!role.getName().startsWith("ROLE_")) {
            role.setName("ROLE_" + role.getName());
        }
        return roleMapper.insertSelective(role);
    }

    public Integer deleteRoleById(Integer rid) {
        return roleMapper.deleteByPrimaryKey(rid);
    }
}
