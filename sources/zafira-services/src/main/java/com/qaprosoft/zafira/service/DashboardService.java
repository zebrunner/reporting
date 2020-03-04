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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.DashboardMapper;
import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.models.dto.user.UserDTO;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.DASHBOARD_CAN_NOT_BE_CREATED;
import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.DASHBOARD_CAN_NOT_BE_UPDATED;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.DASHBOARD_NOT_FOUND;

@Service
public class DashboardService {

    private static final String ERR_MSG_DASHBOARD_CAN_NOT_BE_FOUND = "Dashboard with id %s can not be found";
    private static final String ERR_MSG_DASHBOARD_ALREADY_EXISTS = "Dashboard with such title already exists";
    private static final String ERR_MSG_UNEDITABLE_DASHBOARD_CANT_BE_ALTERED = "Uneditable dashboard can not be updated or deleted";

    private final DashboardMapper dashboardMapper;
    private final UserPreferenceService userPreferenceService;

    public DashboardService(DashboardMapper dashboardMapper, UserPreferenceService userPreferenceService) {
        this.dashboardMapper = dashboardMapper;
        this.userPreferenceService = userPreferenceService;
    }

    @Transactional
    public Dashboard createDashboard(Dashboard dashboard) {
        if (retrieveByTitle(dashboard.getTitle()) != null) {
            throw new IllegalOperationException(DASHBOARD_CAN_NOT_BE_CREATED, ERR_MSG_DASHBOARD_ALREADY_EXISTS);
        }
        dashboard.setEditable(true);
        dashboardMapper.createDashboard(dashboard);
        return dashboard;
    }

    @Transactional(readOnly = true)
    public Dashboard getDashboardById(Long id) {
        Dashboard dashboard = dashboardMapper.getDashboardById(id);
        if (dashboard == null) {
            throw new ResourceNotFoundException(DASHBOARD_NOT_FOUND, ERR_MSG_DASHBOARD_CAN_NOT_BE_FOUND, id);
        }
        return dashboard;
    }

    @Transactional(readOnly = true)
    public List<Dashboard> retrieveAll() {
        return dashboardMapper.getAllDashboards();
    }

    @Transactional(readOnly = true)
    public List<Dashboard> retrieveByVisibility(boolean hidden) {
        return dashboardMapper.getDashboardsByHidden(hidden);
    }

    @Transactional(readOnly = true)
    public Dashboard retrieveByTitle(String title) {
        return dashboardMapper.getDashboardByTitle(title);
    }

    @Transactional(readOnly = true)
    public Dashboard retrieveDefaultForUser(Long userId) {
        return dashboardMapper.getDefaultDashboardByUserId(userId);
    }

    @Transactional
    public Dashboard update(Dashboard updatedDashboard) {
        Dashboard dashboard = getDashboardById(updatedDashboard.getId());
        // only editable dashboard can be modified, throw exception otherwise
        if (dashboard.isEditable()) {
            updatedDashboard.setEditable(true);
            dashboardMapper.updateDashboard(updatedDashboard);

            // if title changed - update user preferences as well
            String currentTitle = dashboard.getTitle();
            String newTitle = updatedDashboard.getTitle();
            if (!currentTitle.equals(newTitle)) {
                userPreferenceService.updateDefaultDashboardPreference(currentTitle, newTitle);
            }
            return updatedDashboard;
        } else {
            throw new IllegalOperationException(DASHBOARD_CAN_NOT_BE_UPDATED, ERR_MSG_UNEDITABLE_DASHBOARD_CANT_BE_ALTERED);
        }
    }

    @Transactional
    public Map<Long, Integer> updateDashboardsOrder(Map<Long, Integer> dashboardsPositions) {
        dashboardsPositions.forEach(dashboardMapper::updateDashboardOrder);
        return dashboardsPositions;
    }

    @Transactional
    public void removeById(Long id) {
        Dashboard dashboard = getDashboardById(id);
        // only editable dashboard can be deleted, throw exception otherwise
        if (dashboard.isEditable()) {
            // reset dashboard preference first, then delete
            userPreferenceService.resetDefaultDashboardPreference(dashboard.getTitle());
            dashboardMapper.deleteDashboardById(id);
        } else {
            throw new IllegalOperationException(DASHBOARD_CAN_NOT_BE_UPDATED, ERR_MSG_UNEDITABLE_DASHBOARD_CANT_BE_ALTERED);
        }
    }

    @Transactional
    public Widget addDashboardWidget(Long dashboardId, Widget widget) {
        dashboardMapper.addDashboardWidget(dashboardId, widget);
        return widget;
    }

    @Transactional
    public Widget updateDashboardWidget(Long dashboardId, Widget widget) {
        dashboardMapper.updateDashboardWidget(dashboardId, widget);
        return widget;
    }

    @Transactional
    public List<Widget> updateDashboardWidgets(Long dashboardId, List<Widget> widgets) {
        return widgets.stream()
                      .map(widget -> updateDashboardWidget(dashboardId, widget))
                      .collect(Collectors.toList());
    }

    @Transactional
    public void removeDashboardWidget(Long dashboardId, Long widgetId) {
        dashboardMapper.deleteDashboardWidget(dashboardId, widgetId);
    }

    @Transactional
    public List<Attribute> retrieveAttributesByDashboardId(Long dashboardId) {
        return dashboardMapper.getAttributesByDashboardId(dashboardId);
    }

    @Transactional
    public Attribute createDashboardAttribute(Long dashboardId, Attribute attribute) {
        dashboardMapper.createDashboardAttribute(dashboardId, attribute);
        return attribute;
    }

    @Transactional
    public Attribute updateAttribute(Attribute attribute) {
        dashboardMapper.updateAttribute(attribute);
        return attribute;
    }

    @Transactional
    public void removeByAttributeById(long attributeId) {
        dashboardMapper.deleteDashboardAttributeById(attributeId);
    }

    @Transactional
    public void setDefaultDashboard(Map<String, Object> extendedUserProfile, String title, String key) {
        Dashboard dashboard;
        if ("defaultDashboardId".equals(key)) {
            dashboard = retrieveDefaultForUser(((UserDTO) extendedUserProfile.get("user")).getId());
        } else {
            dashboard = retrieveByTitle(title);
        }
        if (dashboard == null) {
            extendedUserProfile.put(key, null);
        } else {
            extendedUserProfile.put(key, dashboard.getId());
        }
    }

}
