package com.qaprosoft.zafira.services.services.auth;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.application.User;
import com.qaprosoft.zafira.models.db.management.Tenancy;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.UserService;
import com.qaprosoft.zafira.services.services.management.MngUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VarUserService {

    @Autowired
    private UserService userService;

    @Autowired
    private MngUserService mngUserService;

    public User getUserByUsername(String username) throws ServiceException {
        User user = null;
        if(isManagementSchema()) {
            user = mngUserService.getUserByUsername(username);
        } else {
            user = userService.getUserByUsername(username);
        }
        return user;
    }

    public User getUserById(long id) throws ServiceException {
        User result;
        if(isManagementSchema()) {
            result = mngUserService.getUserById(id);
        } else {
            result = userService.getUserById(id);
        }
        return result;
    }

    public void updateLastLoginDate(long userId) {
        if(isManagementSchema()) {
            mngUserService.updateLastLoginDate(userId);
        } else {
            userService.updateLastLoginDate(userId);
        }
    }

    private boolean isManagementSchema() {
        return TenancyContext.getTenantName().equals(Tenancy.getManagementSchema());
    }
}
