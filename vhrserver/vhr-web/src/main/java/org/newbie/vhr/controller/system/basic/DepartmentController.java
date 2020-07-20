package org.newbie.vhr.controller.system.basic;


import org.newbie.vhr.model.Department;
import org.newbie.vhr.model.RespBean;
import org.newbie.vhr.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/basic/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/")
    public List<Department> getAllDepartment() {
        return departmentService.getAllDepartment();
    }

    /**
     * 使用存储过程 添加
     */
    @PostMapping("/")
    public RespBean addDep(@RequestBody Department dep) {
        departmentService.addDep(dep);
        if (dep.getResult() == 1) {
            return RespBean.ok("添加成功!", dep);
        }
        return RespBean.error("添加失败!");
    }

    @DeleteMapping("/{id}")
    public RespBean deleteDepById(@PathVariable Integer id) {
        Department department = new Department();
        department.setId(id);
        departmentService.deleteDepById(department);
        System.out.println(department.getResult());
        if (department.getResult() == -2) {
            return RespBean.error("该部门下有子部门，删除失败!");
        } else if (department.getResult() == -1) {
            return RespBean.error("该部门下有员工，删除失败!");
        } else if (department.getResult() == 1) {
            return RespBean.ok("删除成功!");
        }
        return RespBean.error("删除失败");
    }
}
