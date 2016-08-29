
package com.qaprosoft.zafira.grid.stf.models;

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
    "iccid",
    "imei",
    "network",
    "phoneNumber"
})
public class Phone {

    /**
     * 
     */
    @JsonProperty("iccid")
    private Object iccid;
    /**
     * 
     */
    @JsonProperty("imei")
    private String imei;
    /**
     * 
     */
    @JsonProperty("network")
    private String network;
    /**
     * 
     */
    @JsonProperty("phoneNumber")
    private Object phoneNumber;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The iccid
     */
    @JsonProperty("iccid")
    public Object getIccid() {
        return iccid;
    }

    /**
     * 
     * @param iccid
     *     The iccid
     */
    @JsonProperty("iccid")
    public void setIccid(Object iccid) {
        this.iccid = iccid;
    }

    /**
     * 
     * @return
     *     The imei
     */
    @JsonProperty("imei")
    public String getImei() {
        return imei;
    }

    /**
     * 
     * @param imei
     *     The imei
     */
    @JsonProperty("imei")
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * 
     * @return
     *     The network
     */
    @JsonProperty("network")
    public String getNetwork() {
        return network;
    }

    /**
     * 
     * @param network
     *     The network
     */
    @JsonProperty("network")
    public void setNetwork(String network) {
        this.network = network;
    }

    /**
     * 
     * @return
     *     The phoneNumber
     */
    @JsonProperty("phoneNumber")
    public Object getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 
     * @param phoneNumber
     *     The phoneNumber
     */
    @JsonProperty("phoneNumber")
    public void setPhoneNumber(Object phoneNumber) {
        this.phoneNumber = phoneNumber;
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
