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

import com.qaprosoft.zafira.dbaccess.utils.SQLAdapter;
import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.models.db.WidgetTemplate;
import com.qaprosoft.zafira.models.dto.QueryParametersDTO;
import com.qaprosoft.zafira.models.dto.widget.WidgetTemplateDTO;
import com.qaprosoft.zafira.models.dto.widget.WidgetDTO;
import com.qaprosoft.zafira.service.WidgetService;
import com.qaprosoft.zafira.service.WidgetTemplateService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApiIgnore
@RequestMapping(path = "api/widgets", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class WidgetController extends AbstractController {

    private final WidgetService widgetService;
    private final WidgetTemplateService widgetTemplateService;
    private final Mapper mapper;

    public WidgetController(WidgetService widgetService,
                            WidgetTemplateService widgetTemplateService,
                            Mapper mapper) {
        this.widgetService = widgetService;
        this.widgetTemplateService = widgetTemplateService;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create widget", nickname = "createWidget", httpMethod = "POST", response = Widget.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PostMapping()
    public WidgetDTO createWidget(@RequestBody @Valid WidgetDTO widgetDTO) {

        Widget widget = mapper.map(widgetDTO, Widget.class);
        return mapper.map(widgetService.createWidget(widget), WidgetDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get widget", nickname = "getWidget", httpMethod = "GET", response = Widget.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/{id}")
    public Widget getWidget(@PathVariable("id") long id) {
        return widgetService.getWidgetById(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Delete widget", nickname = "deleteWidget", httpMethod = "DELETE")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @DeleteMapping("/{id}")
    public void deleteWidget(@PathVariable("id") long id) {
        widgetService.deleteWidgetById(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update widget", nickname = "updateWidget", httpMethod = "PUT", response = Widget.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PutMapping()
    public Widget updateWidget(@RequestBody WidgetDTO widgetDTO) {
        Widget widget = mapper.map(widgetDTO, Widget.class);
        return widgetService.updateWidget(widget);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Execute SQL", nickname = "executeSQL", httpMethod = "POST", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PostMapping("/sql")
    public List<Map<String, Object>> executeSQL(@RequestBody @Valid SQLAdapter sql,
                                                @RequestParam(name = "projects", defaultValue = "", required = false) List<String> projects,
                                                @RequestParam(name = "currentUserId", required = false) String currentUserId,
                                                @RequestParam(name = "dashboardName", required = false) String dashboardName,
                                                @RequestParam(name = "stackTraceRequired", required = false) boolean stackTraceRequired) {
        String query = sql.getSql();
        List<Attribute> attributes = sql.getAttributes();
        return widgetService.getQueryResultObsolete(projects, currentUserId, dashboardName, stackTraceRequired, query, attributes, getPrincipalId(), getPrincipalName());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all widgets", nickname = "getAllWidgets", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping()
    public List<WidgetDTO> getAllWidgets() {
        return widgetService.getAllWidgets()
                            .stream()
                            .map(widget -> {
                                widgetTemplateService.clearRedundantParamsValues(widget.getWidgetTemplate());
                                return mapper.map(widget, WidgetDTO.class);
                            }).collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all widget templates", nickname = "getAllWidgetTemplates", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/templates")
    public List<WidgetTemplateDTO> getAllWidgetTemplates() {
        List<WidgetTemplate> widgetTemplates = widgetTemplateService.getWidgetTemplates();
        return widgetTemplates.stream()
                              .map(widgetTemplate -> mapper.map(widgetTemplate, WidgetTemplateDTO.class))
                              .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Prepare widget template data by id", nickname = "prepareWidgetTemplateById", httpMethod = "GET", response = WidgetTemplateDTO.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/templates/{id}/prepare")
    public WidgetTemplateDTO prepareWidgetTemplate(@PathVariable("id") Long id) {
        return mapper.map(widgetTemplateService.prepareWidgetTemplateById(id), WidgetTemplateDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Execute SQL template", nickname = "executeSQLTemplate", httpMethod = "POST", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PostMapping("/templates/sql")
    public List<Map<String, Object>> executeSQL(@RequestBody @Valid QueryParametersDTO queryParametersDTO,
                                                @RequestParam(value = "stackTraceRequired", required = false) boolean stackTraceRequired) {
        Long templateId = queryParametersDTO.getTemplateId();
        Map<String, Object> params = queryParametersDTO.getParamsConfig();
        return widgetService.getQueryResults(stackTraceRequired, params, templateId, getPrincipalId(), getPrincipalName());
    }

}
