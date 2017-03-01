
package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.View;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ViewMapper
{
	void createView(View view);

	View getViewById(long id);

	List<View> getAllViews(@Param("projectId") Long projectId);

	void updateView(View view);

	void deleteViewById(long id);
}