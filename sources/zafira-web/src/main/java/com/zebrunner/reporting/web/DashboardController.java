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
package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Attribute;
import com.zebrunner.reporting.domain.db.Dashboard;
import com.zebrunner.reporting.domain.db.Permission;
import com.zebrunner.reporting.domain.db.Widget;
import com.zebrunner.reporting.domain.dto.DashboardType;
import com.zebrunner.reporting.service.DashboardService;
import com.zebrunner.reporting.service.WidgetTemplateService;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.web.documented.DashboardDocumentedController;
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

@CrossOrigin
@RequestMapping(path = "api/dashboards", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class DashboardController extends AbstractController implements DashboardDocumentedController {

    private static final String ERR_MSG_ILLEGAL_DASHBOARD_ACCESS = "Cannot access requested dashboard by id '%d'";

    private final DashboardService dashboardService;
    private final WidgetTemplateService widgetTemplateService;
    private final Mapper mapper;

    public DashboardController(DashboardService dashboardService, WidgetTemplateService widgetTemplateService, Mapper mapper) {
        this.dashboardService = dashboardService;
        this.widgetTemplateService = widgetTemplateService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS') and ((hasPermission('VIEW_HIDDEN_DASHBOARDS') and #dashboardType.hidden) or !#dashboardType.hidden)")
    @PostMapping()
    @Override
    public DashboardType createDashboard(@RequestBody @Valid DashboardType dashboardType) {
        Dashboard dashboard = mapper.map(dashboardType, Dashboard.class);
        dashboard = dashboardService.createDashboard(dashboard);
        return mapper.map(dashboard, DashboardType.class);
    }

    @GetMapping()
    @Override
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

    @GetMapping("/{id}")
    @Override
    public DashboardType getDashboardById(@PathVariable("id") long id) {
        Dashboard dashboard = dashboardService.getDashboardById(id);
        if (dashboard.isHidden() && !hasPermission(Permission.Name.VIEW_HIDDEN_DASHBOARDS)) {
            throw new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.DASHBOARD_NOT_FOUND, String.format(ERR_MSG_ILLEGAL_DASHBOARD_ACCESS, id));
        }
        dashboard.getWidgets().forEach(widget -> widgetTemplateService.clearRedundantParamsValues(widget.getWidgetTemplate()));
        return mapper.map(dashboard, DashboardType.class);
    }

    @GetMapping("/title")
    @Override
    public DashboardType getDashboardByTitle(@RequestParam(name = "title", required = false) String title) {
        Dashboard dashboard = dashboardService.retrieveByTitle(title);
        return mapper.map(dashboard, DashboardType.class);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @DeleteMapping("/{id}")
    @Override
    public void deleteDashboard(@PathVariable("id") long id) {
        dashboardService.removeById(id);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PutMapping()
    @Override
    public DashboardType updateDashboard(@Valid @RequestBody DashboardType dashboardType) {
        Dashboard dashboard = mapper.map(dashboardType, Dashboard.class);
        dashboard = dashboardService.update(dashboard);
        return mapper.map(dashboard, DashboardType.class);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PutMapping("/order")
    @Override
    public Map<Long, Integer> updateDashboardsOrder(@RequestBody Map<Long, Integer> order) {
        return dashboardService.updateDashboardsOrder(order);
    }

    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PostMapping("/{dashboardId}/widgets")
    @Override
    public Widget addDashboardWidget(@PathVariable("dashboardId") long dashboardId, @RequestBody Widget widget) {
        return dashboardService.addDashboardWidget(dashboardId, widget);
    }

    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @DeleteMapping("/{dashboardId}/widgets/{widgetId}")
    @Override
    public void deleteDashboardWidget(@PathVariable("dashboardId") long dashboardId, @PathVariable("widgetId") long widgetId) {
        dashboardService.removeDashboardWidget(dashboardId, widgetId);
    }

    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PutMapping("/{dashboardId}/widgets")
    @Override
    public Widget updateDashboardWidget(@PathVariable("dashboardId") long dashboardId, @RequestBody Widget widget) {
        return dashboardService.updateDashboardWidget(dashboardId, widget);
    }

    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PutMapping("/{dashboardId}/widgets/all")
    @Override
    public List<Widget> updateDashboardWidgets(@PathVariable("dashboardId") long dashboardId, @RequestBody List<Widget> widgets) {
        return dashboardService.updateDashboardWidgets(dashboardId, widgets);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PostMapping("/{dashboardId}/attributes")
    @Override
    public List<Attribute> createDashboardAttribute(@PathVariable("dashboardId") long dashboardId, @RequestBody Attribute attribute) {
        dashboardService.createDashboardAttribute(dashboardId, attribute);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PutMapping("/{dashboardId}/attributes")
    @Override
    public List<Attribute> updateDashboardAttribute(@PathVariable("dashboardId") long dashboardId, @RequestBody Attribute attribute) {
        dashboardService.updateAttribute(attribute);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @DeleteMapping("/{dashboardId}/attributes/{id}")
    @Override
    public List<Attribute> deleteDashboardAttribute(@PathVariable("dashboardId") long dashboardId, @PathVariable("id") long id) {
        dashboardService.removeByAttributeById(id);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

}
