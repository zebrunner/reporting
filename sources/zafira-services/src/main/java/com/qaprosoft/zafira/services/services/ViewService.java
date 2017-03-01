package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.ViewMapper;
import com.qaprosoft.zafira.models.db.View;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class ViewService
{
	@Autowired
	private ViewMapper viewMapper;
	
	@Transactional(readOnly = true)
	public List<View> getAllViews(Long projectId) throws ServiceException
	{
		return viewMapper.getAllViews(projectId);
	}
	
	@Transactional(readOnly = true)
	public View getViewById(Long id) throws ServiceException
	{
		return viewMapper.getViewById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public View createView(View view) throws ServiceException
	{
		viewMapper.createView(view);
		return view;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public View updateView(View view) throws ServiceException
	{
		viewMapper.updateView(view);
		return view;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteViewById(long id) throws ServiceException
	{
		viewMapper.deleteViewById(id);
	}
}