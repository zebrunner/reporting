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
package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.models.dto.DashboardType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Api("Dashboards API")
public interface DashboardDocumentedController {

    @ApiOperation(
            value = "Creates dashboard",
            notes = "Returns created editable dashboard",
            nickname = "createDashboard",
            httpMethod = "POST",
            response = DashboardType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardType", paramType = "body", dataType = "DashboardType", required = true, value = "Dashboard to create"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created dashboard", response = DashboardType.class),
            @ApiResponse(code = 400, message = "Indicates that dashboard with same name is already exist", response = ResponseEntity.class)
    })
    DashboardType createDashboard(DashboardType dashboardType);

    @ApiOperation(
            value = "Retrieves all dashboard by visibility",
            notes = "Returns found dashboards. To retrieve hidden dashboards user must to have separate permission",
            nickname = "getAllDashboards",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "hidden", paramType = "query", value = "Flag to retrieve hidden dashboards as well"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found dashboards", response = List.class)
    })
    List<DashboardType> getAllDashboards(boolean hidden);

    @ApiOperation(
            value = "Retrieves dashboard by id",
            notes = "Returns found dashboard if exists",
            nickname = "getDashboardById",
            httpMethod = "GET",
            response = DashboardType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Dashboard id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found dashboard", response = DashboardType.class),
            @ApiResponse(code = 404, message = "Indicates that dashboard by id is not found", response = ResponseEntity.class)
    })
    DashboardType getDashboardById(long id);

    @ApiOperation(
            value = "Retrieves dashboard by name",
            notes = "Returns found dashboard or null if dashboard does not exist",
            nickname = "getDashboardByTitle",
            httpMethod = "GET",
            response = DashboardType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "title", paramType = "query", dataType = "string", value = "Dashboard name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found dashboard", response = DashboardType.class)
    })
    DashboardType getDashboardByTitle(String title);

    @ApiOperation(
            value = "Deletes dashboard by id",
            notes = "Deletes dashboard if it is editable",
            nickname = "deleteDashboard",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Dashboard id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Dashboard was deleted successfully"),
            @ApiResponse(code = 400, message = "Indicates that dashboard is not editable", response = ResponseEntity.class)
    })
    void deleteDashboard(long id);

    @ApiOperation(
            value = "Updates dashboard",
            notes = "Returns updated dashboard",
            nickname = "updateDashboard",
            httpMethod = "PUT",
            response = DashboardType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardType", paramType = "body", dataType = "DashboardType", required = true, value = "Dashboard to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated dashboard", response = DashboardType.class),
            @ApiResponse(code = 404, message = "Indicates that dashboard is not editable", response = ResponseEntity.class)
    })
    DashboardType updateDashboard(DashboardType dashboardType);

    @ApiOperation(
            value = "Updates dashboard visibility order",
            notes = "Returns updated order",
            nickname = "updateDashboardsOrder",
            httpMethod = "PUT",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "order", paramType = "body", dataType = "Map", required = true, value = "New dashboards order to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated dashboards order", response = Map.class)
    })
    Map<Long, Integer> updateDashboardsOrder(Map<Long, Integer> order);

    @ApiOperation(
            value = "Adds widget to dashboard by dashboard id",
            notes = "Widget will be visible on dashboard",
            nickname = "addDashboardWidget",
            httpMethod = "POST",
            response = Widget.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataType = "number", required = true, value = "Dashboard id"),
            @ApiImplicitParam(name = "widget", paramType = "body", dataType = "Widget", required = true, value = "Widget to attach")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns added widget", response = Widget.class)
    })
    Widget addDashboardWidget(long dashboardId, Widget widget);

    @ApiOperation(
            value = "Deletes widget from dashboard",
            notes = "Widget will be unattached from dashboard only",
            nickname = "deleteDashboardWidget",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataType = "number", required = true, value = "Dashboard id"),
            @ApiImplicitParam(name = "widgetId", paramType = "path", dataType = "number", required = true, value = "Widget id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Widget was unattached from dashboard successfully")
    })
    void deleteDashboardWidget(long dashboardId, long widgetId);

    @ApiOperation(
            value = "Updates widgets in dashboard by id",
            notes = "Dashboard id is a double check validation to avoid invalid changes",
            nickname = "updateDashboardWidget",
            httpMethod = "PUT",
            response = Widget.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataType = "number", required = true, value = "Dashboard id"),
            @ApiImplicitParam(name = "widget", paramType = "body", dataType = "number", required = true, value = "Widget to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated widget if it contains in dashboard by id", response = Widget.class)
    })
    Widget updateDashboardWidget(long dashboardId, Widget widget);

    @ApiOperation(
            value = "Batch updates widgets in dashboard by id",
            notes = "Dashboard id is a double check validation to avoid invalid changes",
            nickname = "updateDashboardWidgets",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataType = "number", required = true, value = "Dashboard id"),
            @ApiImplicitParam(name = "widgets", paramType = "body", dataType = "array", required = true, value = "Widgets to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated widgets", response = List.class)
    })
    List<Widget> updateDashboardWidgets(long dashboardId, List<Widget> widgets);

    @ApiOperation(
            value = "Creates a dashboard attribute",
            notes = "Created dashboard attribute will be used on widget`s data search",
            nickname = "createDashboardAttribute",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataType = "number", required = true, value = "Dashboard id which has an attribute"),
            @ApiImplicitParam(name = "attribute", paramType = "body", dataType = "Attribute", required = true, value = "Attribute to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all dashboard attribute included created attribute", response = List.class)
    })
    List<Attribute> createDashboardAttribute(long dashboardId, Attribute attribute);

    @ApiOperation(
            value = "Updates exists dashboard attribute properties",
            notes = "Updated properties will be available for widget`s data search",
            nickname = "updateDashboardAttribute",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataType = "number", required = true, value = "Dashboard id which has an attribute"),
            @ApiImplicitParam(name = "attribute", paramType = "body", dataType = "Attribute", required = true, value = "Attribute to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all dashboard attribute included updated attribute", response = List.class)
    })
    List<Attribute> updateDashboardAttribute(long dashboardId, Attribute attribute);

    @ApiOperation(
            value = "Deletes attribute from dashboard",
            notes = "Deleted attribute will not be available for widget`s data search",
            nickname = "deleteDashboardAttribute",
            httpMethod = "DELETE",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataType = "number", required = true, value = "Dashboard id which has an attribute"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Attribute id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all dashboard attribute excluded updated attribute", response = List.class)
    })
    List<Attribute> deleteDashboardAttribute(long dashboardId, long id);

}
