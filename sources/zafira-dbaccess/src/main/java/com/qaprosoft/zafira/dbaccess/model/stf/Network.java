
package com.qaprosoft.zafira.dbaccess.model.stf;

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
    "connected",
    "failover",
    "manual",
    "operator",
    "roaming",
    "state",
    "subtype",
    "type"
})
public class Network {

    /**
     * 
     */
    @JsonProperty("connected")
    private Boolean connected;
    /**
     * 
     */
    @JsonProperty("failover")
    private Boolean failover;
    /**
     * 
     */
    @JsonProperty("manual")
    private Boolean manual;
    /**
     * 
     */
    @JsonProperty("operator")
    private String operator;
    /**
     * 
     */
    @JsonProperty("roaming")
    private Boolean roaming;
    /**
     * 
     */
    @JsonProperty("state")
    private String state;
    /**
     * 
     */
    @JsonProperty("subtype")
    private String subtype;
    /**
     * 
     */
    @JsonProperty("type")
    private String type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The connected
     */
    @JsonProperty("connected")
    public Boolean getConnected() {
        return connected;
    }

    /**
     * 
     * @param connected
     *     The connected
     */
    @JsonProperty("connected")
    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    /**
     * 
     * @return
     *     The failover
     */
    @JsonProperty("failover")
    public Boolean getFailover() {
        return failover;
    }

    /**
     * 
     * @param failover
     *     The failover
     */
    @JsonProperty("failover")
    public void setFailover(Boolean failover) {
        this.failover = failover;
    }

    /**
     * 
     * @return
     *     The manual
     */
    @JsonProperty("manual")
    public Boolean getManual() {
        return manual;
    }

    /**
     * 
     * @param manual
     *     The manual
     */
    @JsonProperty("manual")
    public void setManual(Boolean manual) {
        this.manual = manual;
    }

    /**
     * 
     * @return
     *     The operator
     */
    @JsonProperty("operator")
    public String getOperator() {
        return operator;
    }

    /**
     * 
     * @param operator
     *     The operator
     */
    @JsonProperty("operator")
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * 
     * @return
     *     The roaming
     */
    @JsonProperty("roaming")
    public Boolean getRoaming() {
        return roaming;
    }

    /**
     * 
     * @param roaming
     *     The roaming
     */
    @JsonProperty("roaming")
    public void setRoaming(Boolean roaming) {
        this.roaming = roaming;
    }

    /**
     * 
     * @return
     *     The state
     */
    @JsonProperty("state")
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 
     * @return
     *     The subtype
     */
    @JsonProperty("subtype")
    public String getSubtype() {
        return subtype;
    }

    /**
     * 
     * @param subtype
     *     The subtype
     */
    @JsonProperty("subtype")
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    /**
     * 
     * @return
     *     The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
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
