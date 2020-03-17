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

import com.zebrunner.reporting.domain.db.Group;
import com.zebrunner.reporting.service.GroupService;
import com.zebrunner.reporting.web.documented.GroupDocumentedController;
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

import java.util.List;

@CrossOrigin
@RequestMapping(path = "api/groups", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class GroupController extends AbstractController implements GroupDocumentedController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @PostMapping()
    @Override
    public Group createGroup(@RequestBody Group group) {
        return groupService.createGroup(group);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @PostMapping("/permissions")
    @Override
    public Group addPermissionsToGroup(@RequestBody Group group) {
        return groupService.addPermissionsToGroup(group);
    }

    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @GetMapping("/{id}")
    @Override
    public Group getGroup(@PathVariable("id") long id) {
        return groupService.getGroupById(id);
    }

    @GetMapping("/all")
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS') or #isPublic")
    @Override
    public List<Group> getAllGroups(@RequestParam(name = "public", required = false) boolean isPublic) {
        return groupService.getAllGroups(isPublic);
    }

    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @GetMapping("/count")
    @Override
    public Integer getGroupsCount() {
        return groupService.getGroupsCount();
    }

    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @GetMapping("/roles")
    @Override
    public List<Group.Role> getRoles() {
        return GroupService.getRoles();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @PutMapping()
    @Override
    public Group updateGroup(@RequestBody Group group) {
        return groupService.updateGroup(group);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @DeleteMapping("/{id}")
    @Override
    public void deleteGroup(@PathVariable("id") long id) {
        groupService.deleteGroup(id);
    }

}
