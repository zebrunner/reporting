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
package com.qaprosoft.zafira.services.exceptions;

import com.qaprosoft.zafira.services.util.LocaleContext;

@SuppressWarnings("rawtypes")
public class EntityAlreadyExistsException extends ServiceException {

    private static final long serialVersionUID = 5062713977366541469L;

    private final String fieldName;

    public EntityAlreadyExistsException(String fieldName) {
        this.fieldName = fieldName;
    }

    public EntityAlreadyExistsException(String fieldName, Class entityClass, boolean showStacktrace) {
        super(buildMessage(fieldName, entityClass), null, showStacktrace);
        this.fieldName = fieldName;
    }

    public EntityAlreadyExistsException(String fieldName, String fieldValue, Class entityClass, boolean showStacktrace) {
        super(buildMessage(fieldName, fieldValue, entityClass), null, showStacktrace);
        this.fieldName = fieldName;
    }

    public EntityAlreadyExistsException(String fieldName, Throwable cause, boolean showStacktrace) {
        super(null, cause, showStacktrace);
        this.fieldName = fieldName;
    }

    public EntityAlreadyExistsException(String fieldName, Throwable cause, Class entityClass, boolean showStacktrace) {
        super(buildMessage(fieldName, entityClass), cause, showStacktrace);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    private static String buildMessage(String fieldName, Class entityClass) {
        return String.format(LocaleContext.getMessage("error.message.entityExistsTemplate"), LocaleContext.getMessage(entityClass), LocaleContext.getMessage("field", fieldName));
    }

    private static String buildMessage(String fieldName, String fieldValue, Class entityClass) {
        return String.format(LocaleContext.getMessage("error.message.entityExistsWithValueTemplate"), LocaleContext.getMessage(entityClass), LocaleContext.getMessage("field", fieldName), fieldValue);
    }
}
