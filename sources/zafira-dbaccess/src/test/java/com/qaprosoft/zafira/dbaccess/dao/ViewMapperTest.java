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
package com.qaprosoft.zafira.dbaccess.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.ViewMapper;
import com.qaprosoft.zafira.dbaccess.utils.Sort;
import com.qaprosoft.zafira.models.db.application.Project;
import com.qaprosoft.zafira.models.db.application.View;

@Test
@ContextConfiguration("classpath:com/qaprosoft/zafira/dbaccess/dbaccess-test.xml")
public class ViewMapperTest extends AbstractTestNGSpringContextTests
{
	@Autowired
	private ViewMapper viewMapper;

	/**
	 * Turn this on to enable this test
	 */
	private static final boolean ENABLED = false;

	private static final Project PROJECT = new Project()
	{
		private static final long serialVersionUID = 1L;
		{
			setName("p1");
			setDescription("d1");
		}
	};

	private static final View VIEW = new View()
	{
		private static final long serialVersionUID = 1L;
		{
			setName("n1");
			setProject(PROJECT);
		}
	};

	@Test(enabled = ENABLED)
	public void createViewTest()
	{
		viewMapper.createView(VIEW);
		Assert.assertNotNull(VIEW.getId(), "");
	}

	@Test(enabled = ENABLED, dependsOnMethods =
	{ "createViewTest" })
	public void getViewByIdTest()
	{
		View view = viewMapper.getViewById(VIEW.getId());
		checkView(view);
	}

	@Test(enabled = ENABLED, dependsOnMethods =
	{ "createViewTest", "getViewByIdTest" })
	public void getAllViewsTest()
	{
		List<View> viewList = viewMapper.getAllViews(null);
		Sort<View> viewSort = new Sort<>();
		viewList = viewSort.sortById(viewList);
		checkView(viewList.get(viewList.size() - 1));
	}

	@Test(enabled = ENABLED, dependsOnMethods =
	{ "createViewTest", "getViewByIdTest", "getAllViewsTest" })
	public void updateViewTest()
	{
		VIEW.setName("n2");
		viewMapper.updateView(VIEW);
		checkView(viewMapper.getViewById(VIEW.getId()));
	}

	@Test(enabled = ENABLED, dependsOnMethods =
	{ "createViewTest", "getViewByIdTest", "getAllViewsTest", "updateViewTest" })
	public void deleteViewByIdTest()
	{
		viewMapper.deleteViewById(VIEW.getId());
		Assert.assertNull(viewMapper.getViewById(VIEW.getId()));
	}

	private void checkView(View view)
	{
		Assert.assertEquals(view.getId(), view.getId(), "");
		Assert.assertEquals(view.getName(), view.getName(), "");
	}
}