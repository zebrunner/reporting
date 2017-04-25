package com.qaprosoft.zafira.services.services.ua;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.UAInspectionMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UAInspectionSearchCriteria;
import com.qaprosoft.zafira.models.db.ua.UAInspection;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class UAInspectionService
{
	@Autowired
	private UAInspectionMapper uaInspectionMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createUAInspection(UAInspection uaInspection) throws ServiceException
	{
		uaInspectionMapper.createUAInspection(uaInspection);
	}
	
	@Transactional(readOnly = true)
	public SearchResult<UAInspection> searchUAInspections(UAInspectionSearchCriteria sc) throws ServiceException
	{
		SearchResult<UAInspection> results = new SearchResult<UAInspection>();
		results.setPage(sc.getPage());
		results.setPageSize(sc.getPageSize());
		results.setSortOrder(sc.getSortOrder());
		results.setResults(uaInspectionMapper.searchUAInspections(sc));
		results.setTotalResults(uaInspectionMapper.getUAInspectionsSearchCount(sc));
		return results;
	}
}
