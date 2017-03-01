package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.models.db.JobView;

public interface JobViewMapper 
{
	void createJobView(JobView jobView);

	JobView getJobViewById(long id);

	List<JobView> getJobViewsByViewId(@Param("viewId") long viewId);

	List<JobView> getJobViewsByViewIdAndEnv(@Param("viewId") long viewId, @Param("env") String env);

	void deleteJobViewById(long id);
	
	void deleteJobViewsByViewIdAndEnv(@Param("viewId") long viewId, @Param("env") String env);
}