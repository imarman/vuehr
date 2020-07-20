package org.newbie.vhr.model;

import java.util.List;

/**
 * 分页查询 封装
 * 保存分页查询的结果
 */
public class RespPageBean {
    // 一共查了多少条记录
    private Long total;
    // 一共查到的对象
    private List<?> data;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
