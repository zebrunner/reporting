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
package com.qaprosoft.zafira.models.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.qaprosoft.zafira.models.db.Permission;

public class PermissionType extends AbstractType {

    private static final long serialVersionUID = 7083932006258442862L;

    @NotEmpty(message = "Name required")
    private Permission.Name name;

    @NotNull(message = "Block required")
    private Permission.Block block;

    public Permission.Name getName()
    {
        return name;
    }

    public void setName(Permission.Name name)
    {
        this.name = name;
    }

    public Permission.Block getBlock()
    {
        return block;
    }

    public void setBlock(Permission.Block block)
    {
        this.block = block;
    }
}
