/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.ViewMapper;
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