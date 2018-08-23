package com.qaprosoft.zafira.services.services.management;

import com.qaprosoft.zafira.dbaccess.dao.mysql.management.TenancyMapper;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.management.Tenancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TenancyService {

    @Autowired
    private TenancyMapper tenancyMapper;

    @Transactional(rollbackFor = Exception.class)
    public Tenancy createTenancy(Tenancy tenancy) {
        tenancyMapper.createTenancy(tenancy);
        return tenancy;
    }

    @Transactional(readOnly = true)
    public Tenancy getTenancyById(Long id) {
        return tenancyMapper.getTenancyById(id);
    }

    @Transactional(readOnly = true)
    public Tenancy getTenancyByName(String name) {
        return tenancyMapper.getTenancyByName(name);
    }

    @Transactional(readOnly = true)
    public List<Tenancy> getTenancies() {
        return tenancyMapper.getTenancies();
    }

    public List<String> getTenancyNames() {
        return getTenancies().stream().map(Tenancy::getName).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public Tenancy updateTenancy(Tenancy tenancy) {
        Tenancy dbTenancy = getTenancyById(tenancy.getId());
        dbTenancy.setName(tenancy.getName());
        tenancyMapper.updateTenancy(dbTenancy);
        return dbTenancy;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTenancyById(Long id) {
        tenancyMapper.deleteTenancyById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTenancyByName(String name) {
        tenancyMapper.deleteTenancyByName(name);
    }

    public void iterateItems(Consumer<Tenancy> tenancyConsumer) {
        getTenancies().forEach(tenancy -> {
            TenancyContext.setTenantName(tenancy.getName());
            tenancyConsumer.accept(tenancy);
            TenancyContext.setTenantName(null);
        });
    }
}
