/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.DashboardMapper;
import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.IllegalOperationException;
import com.qaprosoft.zafira.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.qaprosoft.zafira.services.exceptions.IllegalOperationException.IllegalOperationErrorDetail.DASHBOARD_CAN_NOT_BE_CREATED;
import static com.qaprosoft.zafira.services.exceptions.ResourceNotFoundException.ResourceNotFoundErrorDetail.DASHBOARD_NOT_FOUND;

@Service
public class DashboardService {

    private static final String ERR_MSG_DASHBOARD_CAN_NOT_BE_FOUND = "Dashboard with id %s can not be found";
    private static final String ERR_MSG_DASHBOARD_ALREADY_EXISTS = "Dashboard with such title already exists";

    private final DashboardMapper dashboardMapper;
    private final UserPreferenceService userPreferenceService;

    public DashboardService(DashboardMapper dashboardMapper, UserPreferenceService userPreferenceService) {
        this.dashboardMapper = dashboardMapper;
        this.userPreferenceService = userPreferenceService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Dashboard createDashboard(Dashboard dashboard) {
        if (getDashboardByTitle(dashboard.getTitle()) != null) {
            throw new IllegalOperationException(DASHBOARD_CAN_NOT_BE_CREATED, ERR_MSG_DASHBOARD_ALREADY_EXISTS);
        }
        dashboard.setEditable(true);
        dashboardMapper.createDashboard(dashboard);
        return dashboard;
    }

    @Transactional(readOnly = true)
    public Dashboard getDashboardById(long id) {
        Dashboard dashboard = dashboardMapper.getDashboardById(id);
        if (dashboard == null) {
            throw new ResourceNotFoundException(DASHBOARD_NOT_FOUND, ERR_MSG_DASHBOARD_CAN_NOT_BE_FOUND, id);
        }
        return dashboard;
    }

    @Transactional(readOnly = true)
    public List<Dashboard> getAllDashboards() {
        return dashboardMapper.getAllDashboards();
    }

    @Transactional(readOnly = true)
    public List<Dashboard> getDashboardsByHidden(boolean hidden) {
        return dashboardMapper.getDashboardsByHidden(hidden);
    }

    @Transactional(readOnly = true)
    public Dashboard getDashboardByTitle(String title) {
        return dashboardMapper.getDashboardByTitle(title);
    }

    @Transactional(readOnly = true)
    public Dashboard getDefaultDashboardByUserId(long userId) {
        return dashboardMapper.getDefaultDashboardByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Dashboard updateDashboard(Dashboard dashboard) {
        Dashboard dbDashboard = getDashboardById(dashboard.getId());
        if (dbDashboard == null) {
            throw new ResourceNotFoundException(DASHBOARD_NOT_FOUND, ERR_MSG_DASHBOARD_CAN_NOT_BE_FOUND, dashboard.getId());
        }
        if (!dbDashboard.isEditable()) {
            throw new ForbiddenOperationException("Cannot update not editable dashboard");
        }
        dashboard.setEditable(dbDashboard.isEditable());
        dashboardMapper.updateDashboard(dashboard);
        if (!dbDashboard.getTitle().equals(dashboard.getTitle())) {
            userPreferenceService.updateDefaultDashboardPreference(dbDashboard.getTitle(), dashboard.getTitle());
        }
        return dashboard;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<Long, Integer> updateDashboardsOrder(Map<Long, Integer> order) {
        order.forEach(dashboardMapper::updateDashboardOrder);
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDashboardById(Long id) {
        Dashboard dashboard = getDashboardById(id);
        if (!dashboard.isEditable()) {
            throw new ForbiddenOperationException("Cannot delete not editable dashboard");
        }
        userPreferenceService.updateDefaultDashboardPreference(dashboard.getTitle(), "General");
        dashboardMapper.deleteDashboardById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Widget addDashboardWidget(Long dashboardId, Widget widget) {
        dashboardMapper.addDashboardWidget(dashboardId, widget);
        return widget;
    }

    @Transactional(rollbackFor = Exception.class)
    public Widget updateDashboardWidget(Long dashboardId, Widget widget) {
        dashboardMapper.updateDashboardWidget(dashboardId, widget);
        return widget;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDashboardWidget(Long dashboardId, Long widgetId) {
        dashboardMapper.deleteDashboardWidget(dashboardId, widgetId);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Attribute> getAttributesByDashboardId(long dashboardId) {
        return dashboardMapper.getAttributesByDashboardId(dashboardId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Attribute createDashboardAttribute(long dashboardId, Attribute attribute) {
        dashboardMapper.createDashboardAttribute(dashboardId, attribute);
        return attribute;
    }

    @Transactional(rollbackFor = Exception.class)
    public Attribute updateAttribute(Attribute attribute) {
        dashboardMapper.updateAttribute(attribute);
        return attribute;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDashboardAttributeById(long attributeId) {
        dashboardMapper.deleteDashboardAttributeById(attributeId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setDefaultDashboard(Map<String, Object> extendedUserProfile, String title, String key) {
        Dashboard dashboard;
        if ("defaultDashboardId".equals(key)) {
            dashboard = getDefaultDashboardByUserId(((UserType) extendedUserProfile.get("user")).getId());
        } else {
            dashboard = getDashboardByTitle(title);
        }
        if (dashboard == null) {
            extendedUserProfile.put(key, null);
        } else {
            extendedUserProfile.put(key, dashboard.getId());
        }
    }

}
