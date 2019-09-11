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
package com.qaprosoft.zafira.models.entity.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
//@ToString(exclude = {"integration"})
@Entity
@NamedEntityGraph(name = "integrationSetting.expanded", attributeNodes = {
        @NamedAttributeNode("param")
})
@Table(name = "integration_settings")
public class IntegrationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    private byte[] binaryData;
    private boolean encrypted;

    @OneToOne
    @JoinColumn(name = "integration_param_id")
    private IntegrationParam param;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="integration_id")
    Integration integration;

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof IntegrationSetting) {
            IntegrationSetting integrationSetting = (IntegrationSetting) obj;
            if (integrationSetting.getId() != null && getId() != null) {
                equals = hashCode() == integrationSetting.hashCode();
            } else if (param != null && integrationSetting.getParam() != null) {
                equals = param.equals(integrationSetting.getParam());
            }
        }
        return equals;
    }

}
