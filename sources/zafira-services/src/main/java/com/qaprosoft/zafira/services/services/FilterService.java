package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.FilterMapper;
import com.qaprosoft.zafira.models.db.Filter;
import com.qaprosoft.zafira.models.dto.filter.FilterType;
import com.qaprosoft.zafira.models.dto.filter.StoredSubject;
import com.qaprosoft.zafira.models.dto.filter.Subject;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.util.FreemarkerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FilterService
{

	private static final String TEST_RUN_TEMPLATE = "/filters/test_run_search_data.ftl";
	private static final String TEST_RUN_COUNT_TEMPLATE = "/filters/test_run_search_data.ftl";

	@Autowired
	private FilterMapper filterMapper;

	@Autowired
	private StoredSubject storedSubject;

	@Autowired
	private FreemarkerUtil freemarkerUtil;

	public enum Template
	{
		TEST_RUN_TEMPLATE("/filters/test_run_search_data.ftl"), TEST_RUN_COUNT_TEMPLATE("/filters/test_run_search_count.ftl");

		private String path;

		Template(String path)
		{
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Filter createFilter(Filter filter) throws ServiceException
	{
		filterMapper.createFilter(filter);
		return filter;
	}

	@Transactional(readOnly = true)
	public Filter getFilterById(long id) throws ServiceException
	{
		return filterMapper.getFilterById(id);
	}

	@Transactional(readOnly = true)
	public Filter getFilterByName(String name) throws ServiceException
	{
		return filterMapper.getFilterByName(name);
	}

	@Transactional(readOnly = true)
	public List<Filter> getAllFilters() throws ServiceException
	{
		return filterMapper.getAllFilters();
	}

	@Transactional(readOnly = true)
	public List<Filter> getAllPublicFilters(Long userId)
	{
		return filterMapper.getAllPublicFilters(userId);
	}

	@Transactional(readOnly = true)
	public Integer getFiltersCount() throws ServiceException
	{
		return filterMapper.getFiltersCount();
	}

	@Transactional(rollbackFor = Exception.class)
	public Filter updateFilter(Filter filter, boolean isAdmin) throws ServiceException
	{
		Filter dbFilter = getFilterById(filter.getId());
		if(dbFilter == null)
		{
			throw new ServiceException("No filters found by id: " + filter.getId());
		}
		if(! filter.getName().equals(dbFilter.getName()) && getFilterByName(filter.getName()) != null)
		{
			throw new ServiceException("Filter with name '" + filter.getName() + "' already exists");
		}
		dbFilter.setName(filter.getName());
		dbFilter.setDescription(filter.getDescription());
		dbFilter.setSubject(filter.getSubject());
		dbFilter.setPublicAccess(isAdmin && filter.isPublicAccess());
		filterMapper.updateFilter(dbFilter);
		return dbFilter;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteFilterById(long id)
	{
		filterMapper.deleteFilterById(id);
	}

	public Subject getStoredSubject(Subject.Name name)
	{
		return storedSubject.getSubjectByName(name);
	}

	public String getTemplate(FilterType filter, Template template) throws ServiceException
	{
		return freemarkerUtil.getFreeMarkerTemplateContent(template.getPath(), filter);
	}
}
