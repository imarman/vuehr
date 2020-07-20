package org.newbie.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.newbie.vhr.model.JobLevel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLevelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(JobLevel record);

    int insertSelective(JobLevel record);

    JobLevel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(JobLevel record);

    int updateByPrimaryKey(JobLevel record);

    List<JobLevel> getAllJobLevel();

    Integer deleteJobLevelsByIds(@Param("ids") Integer[] ids);
}