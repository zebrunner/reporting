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

package com.qaprosoft.zafira.models.stf;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "health",
    "level",
    "scale",
    "source",
    "status",
    "temp",
    "voltage"
})
public class Battery {

    /**
     * 
     */
    @JsonProperty("health")
    private String health;
    /**
     * 
     */
    @JsonProperty("level")
    private Double level;
    /**
     * 
     */
    @JsonProperty("scale")
    private Double scale;
    /**
     * 
     */
    @JsonProperty("source")
    private String source;
    /**
     * 
     */
    @JsonProperty("status")
    private String status;
    /**
     * 
     */
    @JsonProperty("temp")
    private Double temp;
    /**
     * 
     */
    @JsonProperty("voltage")
    private Double voltage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The health
     */
    @JsonProperty("health")
    public String getHealth() {
        return health;
    }

    /**
     * 
     * @param health
     *     The health
     */
    @JsonProperty("health")
    public void setHealth(String health) {
        this.health = health;
    }

    /**
     * 
     * @return
     *     The level
     */
    @JsonProperty("level")
    public Double getLevel() {
        return level;
    }

    /**
     * 
     * @param level
     *     The level
     */
    @JsonProperty("level")
    public void setLevel(Double level) {
        this.level = level;
    }

    /**
     * 
     * @return
     *     The scale
     */
    @JsonProperty("scale")
    public Double getScale() {
        return scale;
    }

    /**
     * 
     * @param scale
     *     The scale
     */
    @JsonProperty("scale")
    public void setScale(Double scale) {
        this.scale = scale;
    }

    /**
     * 
     * @return
     *     The source
     */
    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    /**
     * 
     * @param source
     *     The source
     */
    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 
     * @return
     *     The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The temp
     */
    @JsonProperty("temp")
    public Double getTemp() {
        return temp;
    }

    /**
     * 
     * @param temp
     *     The temp
     */
    @JsonProperty("temp")
    public void setTemp(Double temp) {
        this.temp = temp;
    }

    /**
     * 
     * @return
     *     The voltage
     */
    @JsonProperty("voltage")
    public Double getVoltage() {
        return voltage;
    }

    /**
     * 
     * @param voltage
     *     The voltage
     */
    @JsonProperty("voltage")
    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
