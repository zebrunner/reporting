package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.filter.Filter;
import com.zebrunner.reporting.domain.dto.filter.FilterDTO;
import com.zebrunner.reporting.domain.dto.filter.Subject;
import com.zebrunner.reporting.service.FilterService;
import com.zebrunner.reporting.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api("Filters API")
@CrossOrigin
@RequestMapping(path = "api/filters", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FilterController extends AbstractController {

    private final FilterService filterService;
    private final Mapper mapper;

    public FilterController(FilterService filterService, Mapper mapper) {
        this.filterService = filterService;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Creates a filter", nickname = "createFilter", httpMethod = "POST", response = FilterDTO.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PostMapping()
    public FilterDTO createFilter(@RequestBody @Valid FilterDTO filterDTO) {
        filterDTO.getSubject().sortCriterias();
        Long principalId = getPrincipalId();
        boolean isAdmin = isAdmin();
        Filter filter = mapper.map(filterDTO, Filter.class);
        Filter createdFilter = filterService.createFilter(filter, principalId, isAdmin);
        return mapper.map(createdFilter, FilterDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves all public filters", nickname = "getAllPublicFilters", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/all/public")
    public List<FilterDTO> getAllPublicFilters() {
        Long principalId = getPrincipalId();
        List<Filter> publicFilters = filterService.getAllPublicFilters(principalId);
        return publicFilters.stream()
                            .map(filter -> mapper.map(filter, FilterDTO.class))
                            .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Updates a filter", nickname = "updateFilter", httpMethod = "PUT", response = FilterDTO.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PutMapping()
    public FilterDTO updateFilter(@RequestBody @Valid FilterDTO filterDTO) {
        filterDTO.getSubject().sortCriterias();
        Long principalId = getPrincipalId();
        boolean isAdmin = isAdmin();
        Filter filter = mapper.map(filterDTO, Filter.class);
        Filter updatedFilter = filterService.updateFilter(filter, principalId, isAdmin);
        return mapper.map(updatedFilter, FilterDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Deletes a filter", nickname = "deleteFilter", httpMethod = "DELETE")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @DeleteMapping("/{id}")
    public void deleteFilter(@PathVariable("id") Long id) {
        Long principalId = getPrincipalId();
        filterService.deleteFilterById(id, principalId);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves a filter builder", nickname = "getBuilder", httpMethod = "GET", response = Subject.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/{name}/builder")
    public Subject getBuilder(@PathVariable("name") Subject.Name name) {
        return filterService.getStoredSubject(name);
    }

}
