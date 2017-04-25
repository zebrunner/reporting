package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UAInspectionSearchCriteria;
import com.qaprosoft.zafira.models.db.ua.UAInspection;

public interface UAInspectionMapper
{
	void createUAInspection(UAInspection uaInspection);
	
	List<UAInspection> searchUAInspections(UAInspectionSearchCriteria sc);

	Integer getUAInspectionsSearchCount(UAInspectionSearchCriteria sc);
}
