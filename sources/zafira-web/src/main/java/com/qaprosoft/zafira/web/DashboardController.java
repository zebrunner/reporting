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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.models.db.Permission;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.models.dto.DashboardType;
import com.qaprosoft.zafira.service.DashboardService;
import com.qaprosoft.zafira.service.WidgetTemplateService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api("Dashboards API")
@CrossOrigin
@RequestMapping(path = "api/dashboards", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class DashboardController extends AbstractController {

    private final DashboardService dashboardService;
    private final WidgetTemplateService widgetTemplateService;
    private final Mapper mapper;

    public DashboardController(DashboardService dashboardService, WidgetTemplateService widgetTemplateService, Mapper mapper) {
        this.dashboardService = dashboardService;
        this.widgetTemplateService = widgetTemplateService;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create dashboard", nickname = "createDashboard", httpMethod = "POST", response = Dashboard.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PostMapping()
    public DashboardType createDashboard(@RequestBody @Valid DashboardType dashboardType) {
        Dashboard dashboard = mapper.map(dashboardType, Dashboard.class);
        dashboard = dashboardService.createDashboard(dashboard);
        return mapper.map(dashboard, DashboardType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get dashboards", nickname = "getAllDashboards", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping()
    public List<DashboardType> getAllDashboards(@RequestParam(value = "hidden", required = false) boolean hidden) {
        List<Dashboard> dashboards;
        if (!hidden && hasPermission(Permission.Name.VIEW_HIDDEN_DASHBOARDS)) {
            dashboards = dashboardService.retrieveAll();
        } else {
            dashboards = dashboardService.retrieveByVisibility(false);
        }

        return dashboards.stream()
                         .map(dashboard -> mapper.map(dashboard, DashboardType.class))
                         .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get dashboard by ID", nickname = "getDashboardById", httpMethod = "GET", response = Dashboard.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/{id}")
    public DashboardType getDashboardById(@PathVariable("id") long id) {
        Dashboard dashboard = dashboardService.getDashboardById(id);
        dashboard.getWidgets().forEach(widget -> widgetTemplateService.clearRedundantParamsValues(widget.getWidgetTemplate()));
        return mapper.map(dashboard, DashboardType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get dashboard by title", nickname = "getDashboardByTitle", httpMethod = "GET", response = Dashboard.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/title")
    public DashboardType getDashboardByTitle(@RequestParam(name = "title", required = false) String title) {
        Dashboard dashboard = dashboardService.retrieveByTitle(title);
        return mapper.map(dashboard, DashboardType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Delete dashboard", nickname = "deleteDashboard", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @DeleteMapping("/{id}")
    public void deleteDashboard(@PathVariable("id") long id) {
        dashboardService.removeById(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update dashboard", nickname = "updateDashboard", httpMethod = "PUT", response = Dashboard.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PutMapping()
    public DashboardType updateDashboard(@Valid @RequestBody DashboardType dashboardType) {
        Dashboard dashboard = mapper.map(dashboardType, Dashboard.class);
        dashboard = dashboardService.update(dashboard);
        return mapper.map(dashboard, DashboardType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update dashboards order", nickname = "updateDashboardsOrder", httpMethod = "PUT", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PutMapping("/order")
    public Map<Long, Integer> updateDashboardsOrder(@RequestBody Map<Long, Integer> order) {
        return dashboardService.updateDashboardsOrder(order);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Add dashboard widget", nickname = "addDashboardWidget", httpMethod = "POST", response = Widget.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PostMapping("/{dashboardId}/widgets")
    public Widget addDashboardWidget(@PathVariable("dashboardId") long dashboardId, @RequestBody Widget widget) {
        return dashboardService.addDashboardWidget(dashboardId, widget);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Delete dashboard widget", nickname = "deleteDashboardWidget", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @DeleteMapping("/{dashboardId}/widgets/{widgetId}")
    public void deleteDashboardWidget(@PathVariable("dashboardId") long dashboardId, @PathVariable("widgetId") long widgetId) {
        dashboardService.removeDashboardWidget(dashboardId, widgetId);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update dashboard widget", nickname = "updateDashboardWidget", httpMethod = "PUT", response = Widget.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PutMapping("/{dashboardId}/widgets")
    public Widget updateDashboardWidget(@PathVariable("dashboardId") long dashboardId, @RequestBody Widget widget) {
        return dashboardService.updateDashboardWidget(dashboardId, widget);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update dashboard widget", nickname = "updateDashboardWidget", httpMethod = "PUT", response = Widget.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PutMapping("/{dashboardId}/widgets/all")
    public List<Widget> updateDashboardWidgets(@PathVariable("dashboardId") long dashboardId, @RequestBody List<Widget> widgets) {
        return dashboardService.updateDashboardWidgets(dashboardId, widgets);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create dashboard attribute", nickname = "createDashboardAttribute", httpMethod = "POST", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PostMapping("/{dashboardId}/attributes")
    public List<Attribute> createDashboardAttribute(@PathVariable("dashboardId") long dashboardId, @RequestBody Attribute attribute) {
        dashboardService.createDashboardAttribute(dashboardId, attribute);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update dashboard attribute", nickname = "createDashboardAttribute", httpMethod = "PUT", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PutMapping("/{dashboardId}/attributes")
    public List<Attribute> updateDashboardAttribute(@PathVariable("dashboardId") long dashboardId, @RequestBody Attribute attribute) {
        dashboardService.updateAttribute(attribute);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Delete dashboard attribute", nickname = "createDashboardAttribute", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @DeleteMapping("/{dashboardId}/attributes/{id}")
    public List<Attribute> deleteDashboardAttribute(@PathVariable("dashboardId") long dashboardId, @PathVariable("id") long id) {
        dashboardService.removeByAttributeById(id);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

}
