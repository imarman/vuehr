package org.newbie.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.newbie.vhr.model.HrRole;
import org.springframework.stereotype.Repository;

@Repository
public interface HrRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HrRole record);

    int insertSelective(HrRole record);

    HrRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HrRole record);

    int updateByPrimaryKey(HrRole record);

    void deleteByHrId(Integer hrId);

    Integer addRole(@Param("hrid") Integer hrid, @Param("rids") Integer[] rids);
}