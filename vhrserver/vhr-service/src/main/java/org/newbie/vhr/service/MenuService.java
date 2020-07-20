package org.newbie.vhr.service;

import org.newbie.vhr.model.Hr;
import org.newbie.vhr.model.Menu;
import org.newbie.vhr.mapper.MenuMapper;
import org.newbie.vhr.mapper.MenuRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private MenuRoleMapper menuRoleMapper;

    /**
     * 根据登陆用户的 id 动态获取菜单项
     */
    public List<Menu> getMenusByHrId() {
        // 参数是当前登录的对象，这个对象不能用前端获取传来的参数的对象，得在后台获取
        return menuMapper.getMenusByHrId(((Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
    }

    // 缓存， 因为权限不怎么会发生变化
//    @Cacheable
    public List<Menu> getAllMenusWithRole() {
        return menuMapper.getAllMenusWithRole();
    }

    public List<Menu> getAllMenus() {
        return menuMapper.getAllMenus();
    }

    public List<Integer> getMidsByRid(Integer rid) {
        return menuMapper.getMidsByRid(rid);
    }

    /**
     * 删除在重新添加  于此完成更新任务
     * 要添加事务，不然删除完了之后没添加成功就麻烦了
     */
    @Transactional
    public boolean updateMenuRole(Integer rid, Integer[] mids) {
        menuRoleMapper.deleteByRid(rid);
        if (mids == null || mids.length == 0) {
            return true;
        }
        Integer result = menuRoleMapper.insertRecord(rid, mids);
        return result == mids.length;
    }
}
