package org.newbie.vhr.controller.emp;

import org.newbie.vhr.model.*;
import org.newbie.vhr.service.*;
import org.newbie.vhr.utils.POIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/employee/basic")
public class EmpBasicController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private NationService nationService;

    @Autowired
    private PoliticsStatusService politicsStatusService;

    @Autowired
    private JobLevelService jobLevelService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/")
    public RespPageBean getEmployeeByPage(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          Employee employee, Date[] beginDateScope) {

        return employeeService.getEmployeeByPage(page, size, employee, beginDateScope);
    }

    @PostMapping("/")
    public RespBean addEmp(@RequestBody Employee employee) {
        if (employeeService.addEmp(employee) == 1) {
            return RespBean.ok("添加成功！");
        }
        return RespBean.error("添加失败！");
    }

    /**
     * 民族
     */
    @GetMapping("/nations")
    public List<Nation> getAllNation() {
        return nationService.getAllNation();
    }

    /**
     * 政治面貌
     */
    @GetMapping("/politicsstatus")
    public List<Politicsstatus> getAllPoliticsStatus() {
        return politicsStatusService.getAllPoliticsStatus();
    }

    /**
     * 职称
     */
    @GetMapping("/joblevel")
    public List<JobLevel> getAllJobLevel() {
        return jobLevelService.getAllJobLevel();
    }

    /**
     * 职位
     */
    @GetMapping("/positions")
    public List<Position> getAllPosition() {
        return positionService.getAllPosition();
    }

    /**
     * 工号
     */
    @GetMapping("/maxwordid")
    public RespBean maxWorkId() {
        return RespBean.build()
                .setStatus(200)
                .setObj(String.format("%08d", employeeService.maxWorkId() + 1));
    }

    /**
     * 获取所有部门
     */
    @GetMapping("/deps")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartment();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public RespBean deleteEpmById(@PathVariable Integer id) {
        if (employeeService.deleteEpmById(id) == 1) {
            return RespBean.ok("删除成功");
        }
        return RespBean.error("删除失败");
    }

    /**
     * 更新修改
     */
    @PutMapping("/")
    public RespBean updateEmp(@RequestBody Employee employee) {
        if (employeeService.updateEmp(employee) == 1) {
            return RespBean.ok("更新成功");
        }
        return RespBean.error("更新失败");
    }

    /**
     * 导出数据
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData() {
        List<Employee> list = (List<Employee>) employeeService.getEmployeeByPage(null, null, null, null).getData();
        return POIUtils.employee2Excel(list);
    }

    /**
     * 导入数据
     */
    @PostMapping("/import")
    public RespBean importData(MultipartFile file) {
        List<Employee> list = POIUtils.excel2Employee(file, nationService.getAllNation(), politicsStatusService.getAllPoliticsStatus(),
                departmentService.getAllDepartmentWithOutChildren(), positionService.getAllPosition(), jobLevelService.getAllJobLevel());
        if (employeeService.addEmps(list) == list.size()) {
            return RespBean.ok("上传成功!");
        }
        return RespBean.error("上传失败!");
    }
}
