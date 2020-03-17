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
package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.Group;
import com.zebrunner.reporting.domain.db.Permission;
import com.zebrunner.reporting.domain.db.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JWTService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTService.class);

    private final String secret;
    private final Integer authTokenExp;
    private final Integer refreshTokenExp;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    public JWTService(
            @Value("${auth.token.secret}") String secret,
            @Value("${auth.token.expiration}") Integer authTokenExp,
            @Value("${auth.token.refresh-expiration}") Integer refreshTokenExp
    ) {
        this.secret = secret;
        this.authTokenExp = authTokenExp;
        this.refreshTokenExp = refreshTokenExp;
    }

    public String generateAuthToken(final User user, final String tenant) {
        Claims claims = Jwts.claims().setSubject(user.getId().toString());
        claims.put("username", user.getUsername());
        List<Long> groupIds = user.getGroups().stream().map(Group::getId).collect(Collectors.toList());
        claims.put("groupIds", groupIds);
        claims.put("tenant", tenant);
        return buildToken(claims, authTokenExp);
    }

    /**
     * Parses user details from JWT token.
     * 
     * @param token
     *            - to parse
     * @return retrieved user details
     */
    public User parseAuthToken(String token) {
        Claims jwtBody = getTokenBody(token);
        User user = new User();
        Long userId = Long.valueOf(jwtBody.getSubject());
        user.setId(userId);
        user.setUsername(jwtBody.get("username", String.class));
        List groupIds = (List) jwtBody.get("groupIds");
        user.setGroups(retrieveUserGroups(groupIds));
        user.setStatus(userService.getUserByIdTrusted(userId).getStatus());
        user.setTenant(jwtBody.get("tenant", String.class));
        return user;
    }

    @SuppressWarnings("unchecked")
    private List<Group> retrieveUserGroups(List groupIds) {
        if (groupIds == null) {
            throw new MalformedJwtException("Group id list is required to authenticate");
        }
        return (List<Group>) groupIds.stream()
                                     .map(groupId -> groupService.getGroupById(((Number) groupId).longValue()))
                                     .collect(Collectors.toList());
    }

    public User parseRefreshToken(final String token) {
        Claims body = getTokenBody(token);
        User user = new User();
        user.setId(Long.valueOf(body.getSubject()));
        user.setPassword(body.get("password", String.class));
        user.setTenant(body.get("tenant", String.class));
        return user;
    }

    /**
     * Generates JWT refresh token storing id, username, password of the user and specifies expiration date.
     * 
     * @param user
     *            - for token refresh
     * @return generated JWT token
     */
    public String generateRefreshToken(final User user, final String tenant) {
        Claims claims = Jwts.claims().setSubject(user.getId().toString());
        claims.put("password", user.getPassword());
        claims.put("tenant", tenant);
        return buildToken(claims, refreshTokenExp);
    }

    /**
     * Generates JWT access token storing id, password of the user and specifies expiration (that never expires).
     * 
     * @param user
     *            - for token generation
     * @return generated JWT token
     */
    public String generateAccessToken(User user, String tenant) {
        Claims claims = Jwts.claims().setSubject(user.getId().toString());
        claims.put("password", user.getPassword());
        claims.put("tenant", tenant);
        return buildToken(claims, Integer.MAX_VALUE);
    }

    private String buildToken(Claims claims, Integer exp) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, exp);
        return Jwts.builder()
                   .setClaims(claims)
                   .signWith(SignatureAlgorithm.HS512, secret)
                   .setExpiration(calendar.getTime())
                   .compact();
    }

    @SuppressWarnings("unchecked")
    public boolean checkPermissions(String tenantName, String token, Set<Permission.Name> permissionsToCheck) {
        Set<Permission.Name> permissions = null;
        String tenant = null;
        try {
            Claims jwtBody = getTokenBody(token);
            tenant = jwtBody.get("tenant", String.class);
            List<Long> groupIds = (List<Long>) jwtBody.get("groupIds");
            permissions = collectPermissions(groupIds);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return tenant != null && tenant.equals(tenantName) && ! CollectionUtils.isEmpty(permissions) && permissions.containsAll(permissionsToCheck);
    }

    private Set<Permission.Name> collectPermissions(List<Long> groupIds) {
        List<Group> groups = retrieveUserGroups(groupIds);
        return groups.stream()
                     .map(Group::getPermissions)                         // collect group permissions
                     .flatMap(Collection::stream)                        // flatten all permission
                     .distinct()                                         // keep unique permissions only
                     .map(Permission::getName)
                     .collect(Collectors.toSet());
    }

    private Claims getTokenBody(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Integer getExpiration() {
        return authTokenExp;
    }

}