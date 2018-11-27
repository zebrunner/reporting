/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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
package com.qaprosoft.zafira.services.services.management;

import com.qaprosoft.zafira.dbaccess.dao.mysql.management.TenancyMapper;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Tenancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@Service
public class TenancyService {

    @Autowired
    private TenancyMapper tenancyMapper;

    @Value("#{new Boolean('${zafira.multitenant}')}")
    private Boolean isMultitenant;

    @Transactional(readOnly = true)
    public List<Tenancy> getAllTenancies() {
        return tenancyMapper.getAllTenancies();
    }

    public void iterateItems(Runnable runnable) {
        if(isMultitenant) {
            iterateItems(tenancy -> runnable.run());
        } else {
            runnable.run();
        }
    }

    private void iterateItems(Consumer<Tenancy> tenancyConsumer) {
        getAllTenancies().forEach(tenancy -> {
            TenancyContext.setTenantName(tenancy.getName());
            tenancyConsumer.accept(tenancy);
            TenancyContext.setTenantName(null);
        });
    }
}
