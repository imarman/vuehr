package org.newbie.vhr.service;

import org.newbie.vhr.model.Hr;
import org.newbie.vhr.mapper.HrMapper;
import org.newbie.vhr.mapper.HrRoleMapper;
import org.newbie.vhr.utils.HrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 在 UserDetailService 中自定义加载用户信息，
 * 并将用户角色 role 相关的所有 Permissions 设置到 Authentication 的 authorities中
 * 以供 PermissionEvaluator对 用户权限进行判断。
 */
@Service
public class HrService implements UserDetailsService {

    @Autowired
    private HrMapper hrMapper;

    @Autowired
    private HrRoleMapper hrRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过 username 字段从数据库获取 hr 的信息
        Hr hr = hrMapper.loadUserByUsername(username);
        // 如果没有找到 hr 抛出异常， 找到则返回
        if (hr == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 登陆成功了，给用户设置角色
        hr.setRoles(hrMapper.getHrRolesById(hr.getId()));
        return hr;
    }

    /**
     * 操作员管理 页面、
     * 查找除了自己以外的
     * 如果输入关键字搜索用户，就用 keyword 模糊查询
     */
    public List<Hr> getAllHrs(String keywords) {
        Hr currentHr = HrUtils.getCurrentHr();
        return hrMapper.getAllHrs(currentHr.getId(), keywords);
    }

    public Integer updateHr(Hr hr) {
        return hrMapper.updateByPrimaryKeySelective(hr);
    }

    /**
     * 更新用户角色
     *      先删除在在添加
     */
    @Transactional
    public boolean updateHrRole(Integer hrId, Integer[] rids) {
        hrRoleMapper.deleteByHrId(hrId);
        return hrRoleMapper.addRole(hrId, rids) == rids.length;
    }

    public Integer deleteHrById(Integer id) {
        return hrMapper.deleteByPrimaryKey(id);
    }

    public List<Hr> getAllHrsExceptCurrentHr() {
        return hrMapper.getAllHrsExceptCurrentHr(HrUtils.getCurrentHr().getId());
    }

    public boolean updateHrPassword(String oldpass, String pass, Integer hrid) {
        Hr hr = hrMapper.selectByPrimaryKey(hrid);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 验证旧密码对不对
        if (encoder.matches(oldpass, hr.getPassword())) {
            String encodePass = encoder.encode(pass);
            Integer result = hrMapper.updatePassword(hrid, encodePass);
            return result == 1;
        }
        return false;
    }
}

