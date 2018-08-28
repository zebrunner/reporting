package com.qaprosoft.zafira.services.services.management;

import com.qaprosoft.zafira.dbaccess.dao.mysql.management.MngTenancyMapper;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Tenancy;
import com.qaprosoft.zafira.services.exceptions.EntityIsAlreadyExistsException;
import com.qaprosoft.zafira.services.exceptions.EntityIsNotExistsException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class MngTenancyService {

    @Autowired
    private MngTenancyMapper mngTenancyMapper;

    @Transactional(rollbackFor = Exception.class)
    public Tenancy createTenancy(Tenancy tenancy) throws ServiceException {
        checkIsTenancyExists(tenancy);
        mngTenancyMapper.createTenancy(tenancy);
        return tenancy;
    }

    @Transactional(readOnly = true)
    public Tenancy getTenancyById(Long id) {
        return mngTenancyMapper.getTenancyById(id);
    }

    @Transactional(readOnly = true)
    public Tenancy getTenancyByName(String name) {
        return mngTenancyMapper.getTenancyByName(name);
    }

    @Transactional(readOnly = true)
    public List<Tenancy> getAllTenancies() {
        return mngTenancyMapper.getAllTenancies();
    }

    public List<String> getTenancyNames() {
        return getAllTenancies().stream().map(Tenancy::getName).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public Tenancy updateTenancy(Tenancy tenancy) throws ServiceException {
        Tenancy dbTenancy = checkIsTenancyNotExists(tenancy.getId());
        checkIsTenancyExists(tenancy);
        dbTenancy.setName(tenancy.getName());
        mngTenancyMapper.updateTenancy(dbTenancy);
        return dbTenancy;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTenancyById(Long id) {
        mngTenancyMapper.deleteTenancyById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTenancyByName(String name) {
        mngTenancyMapper.deleteTenancyByName(name);
    }

    public void iterateItems(Consumer<Tenancy> tenancyConsumer) {
        getAllTenancies().forEach(tenancy -> {
            TenancyContext.setTenantName(tenancy.getName());
            tenancyConsumer.accept(tenancy);
            TenancyContext.setTenantName(null);
        });
    }

    private boolean isTenancyExists(Tenancy tenancy) {
        return getTenancyByName(tenancy.getName()) != null;
    }

    private void checkIsTenancyExists(Tenancy tenancy) throws EntityIsAlreadyExistsException {
        Tenancy dbTenancy = getTenancyByName(tenancy.getName());
        if((tenancy.getId() == null && dbTenancy != null) || (dbTenancy != null && tenancy.getId() != null && ! tenancy.getId().equals(dbTenancy.getId()))) {
            throw new EntityIsAlreadyExistsException("name", Tenancy.class);
        }
    }

    private Tenancy checkIsTenancyNotExists(Long id) throws EntityIsNotExistsException {
        Tenancy dbTenancy = getTenancyById(id);
        if(dbTenancy == null) {
            throw new EntityIsNotExistsException(Tenancy.class);
        }
        return dbTenancy;
    }
}
