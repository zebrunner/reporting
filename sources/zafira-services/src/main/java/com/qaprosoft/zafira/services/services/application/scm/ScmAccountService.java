/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.services.application.scm;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.ScmAccountMapper;
import com.qaprosoft.zafira.models.db.ScmAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScmAccountService {

    @Autowired
    private ScmAccountMapper scmAccountMapper;

    @Transactional(rollbackFor = Exception.class)
    public ScmAccount createScmAccount(ScmAccount scmAccount) {
        scmAccountMapper.createScmAccount(scmAccount);
        return scmAccount;
    }

    @Transactional(readOnly = true)
    public ScmAccount getScmAccountById(Long id) {
        return scmAccountMapper.getScmAccountById(id);
    }

    @Transactional(readOnly = true)
    public List<ScmAccount> getAllScmAccounts() {
        return scmAccountMapper.getAllScmAccounts();
    }

    @Transactional(rollbackFor = Exception.class)
    public ScmAccount updateScmAccount(ScmAccount scmAccount) {
        scmAccountMapper.updateScmAccount(scmAccount);
        return scmAccount;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteScmAccountById(Long id) {
        scmAccountMapper.deleteScmAccountById(id);
    }
}
