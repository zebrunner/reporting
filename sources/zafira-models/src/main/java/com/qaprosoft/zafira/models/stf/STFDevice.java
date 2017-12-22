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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "abi",
    "airplaneMode",
    "battery",
    "browser",
    "channel",
    "createdAt",
    "display",
    "manufacturer",
    "model",
    "network",
    "operator",
    "owner",
    "phone",
    "platform",
    "presenceChangedAt",
    "present",
    "product",
    "provider",
    "ready",
    "remoteConnectUrl",
    "remoteConnect",
    "reverseForwards",
    "sdk",
    "serial",
    "statusChangedAt",
    "status",
    "using",
    "version",
    "deviceType"
})
public class STFDevice {

    /**
     * 
     */
    @JsonProperty("abi")
    private String abi;
    /**
     * 
     */
    @JsonProperty("airplaneMode")
    private Boolean airplaneMode;
    /**
     * 
     */
    @JsonProperty("battery")
    private Battery battery;
    /**
     * 
     */
    @JsonProperty("browser")
    private Browser browser;
    /**
     * 
     */
    @JsonProperty("channel")
    private String channel;
    /**
     * 
     */
    @JsonProperty("createdAt")
    private String createdAt;
    /**
     * 
     */
    @JsonProperty("display")
    private Display display;
    /**
     * 
     */
    @JsonProperty("manufacturer")
    private String manufacturer;
    /**
     * 
     */
    @JsonProperty("model")
    private String model;
    /**
     * 
     */
    @JsonProperty("network")
    private Network network;
    /**
     * 
     */
    @JsonProperty("operator")
    private Object operator;
    /**
     * 
     */
    @JsonProperty("owner")
    private Object owner;
    /**
     * 
     */
    @JsonProperty("phone")
    private Phone phone;
    /**
     * 
     */
    @JsonProperty("platform")
    private String platform;
    /**
     * 
     */
    @JsonProperty("presenceChangedAt")
    private String presenceChangedAt;
    /**
     * 
     */
    @JsonProperty("present")
    private Boolean present;
    /**
     * 
     */
    @JsonProperty("product")
    private String product;
    /**
     * 
     */
    @JsonProperty("provider")
    private Provider provider;
    /**
     * 
     */
    @JsonProperty("ready")
    private Boolean ready;
    /**
     * 
     */
    @JsonProperty("remoteConnectUrl")
    private Object remoteConnectUrl;
    /**
     * 
     */
    @JsonProperty("remoteConnect")
    private Boolean remoteConnect;
    /**
     * 
     */
    @JsonProperty("reverseForwards")
    private List<Object> reverseForwards = new ArrayList<Object>();
    /**
     * 
     */
    @JsonProperty("sdk")
    private String sdk;
    /**
     * 
     */
    @JsonProperty("serial")
    private String serial;
    /**
     * 
     */
    @JsonProperty("statusChangedAt")
    private String statusChangedAt;
    /**
     * 
     */
    @JsonProperty("status")
    private Double status;
    /**
     * 
     */
    @JsonProperty("using")
    private Boolean using;
    /**
     * 
     */
    @JsonProperty("version")
    private String version;
    /**
     * 
     */
    @JsonProperty("deviceType")
    private String deviceType = "Phone";
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The abi
     */
    @JsonProperty("abi")
    public String getAbi() {
        return abi;
    }

    /**
     * 
     * @param abi
     *     The abi
     */
    @JsonProperty("abi")
    public void setAbi(String abi) {
        this.abi = abi;
    }

    /**
     * 
     * @return
     *     The airplaneMode
     */
    @JsonProperty("airplaneMode")
    public Boolean getAirplaneMode() {
        return airplaneMode;
    }

    /**
     * 
     * @param airplaneMode
     *     The airplaneMode
     */
    @JsonProperty("airplaneMode")
    public void setAirplaneMode(Boolean airplaneMode) {
        this.airplaneMode = airplaneMode;
    }

    /**
     * 
     * @return
     *     The battery
     */
    @JsonProperty("battery")
    public Battery getBattery() {
        return battery;
    }

    /**
     * 
     * @param battery
     *     The battery
     */
    @JsonProperty("battery")
    public void setBattery(Battery battery) {
        this.battery = battery;
    }

    /**
     * 
     * @return
     *     The browser
     */
    @JsonProperty("browser")
    public Browser getBrowser() {
        return browser;
    }

    /**
     * 
     * @param browser
     *     The browser
     */
    @JsonProperty("browser")
    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    /**
     * 
     * @return
     *     The channel
     */
    @JsonProperty("channel")
    public String getChannel() {
        return channel;
    }

    /**
     * 
     * @param channel
     *     The channel
     */
    @JsonProperty("channel")
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The createdAt
     */
    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The display
     */
    @JsonProperty("display")
    public Display getDisplay() {
        return display;
    }

    /**
     * 
     * @param display
     *     The display
     */
    @JsonProperty("display")
    public void setDisplay(Display display) {
        this.display = display;
    }

    /**
     * 
     * @return
     *     The manufacturer
     */
    @JsonProperty("manufacturer")
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * 
     * @param manufacturer
     *     The manufacturer
     */
    @JsonProperty("manufacturer")
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * 
     * @return
     *     The model
     */
    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    /**
     * 
     * @param model
     *     The model
     */
    @JsonProperty("model")
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 
     * @return
     *     The network
     */
    @JsonProperty("network")
    public Network getNetwork() {
        return network;
    }

    /**
     * 
     * @param network
     *     The network
     */
    @JsonProperty("network")
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * 
     * @return
     *     The operator
     */
    @JsonProperty("operator")
    public Object getOperator() {
        return operator;
    }

    /**
     * 
     * @param operator
     *     The operator
     */
    @JsonProperty("operator")
    public void setOperator(Object operator) {
        this.operator = operator;
    }

    /**
     * 
     * @return
     *     The owner
     */
    @JsonProperty("owner")
    public Object getOwner() {
        return owner;
    }

    /**
     * 
     * @param owner
     *     The owner
     */
    @JsonProperty("owner")
    public void setOwner(Object owner) {
        this.owner = owner;
    }

    /**
     * 
     * @return
     *     The phone
     */
    @JsonProperty("phone")
    public Phone getPhone() {
        return phone;
    }

    /**
     * 
     * @param phone
     *     The phone
     */
    @JsonProperty("phone")
    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    /**
     * 
     * @return
     *     The platform
     */
    @JsonProperty("platform")
    public String getPlatform() {
        return platform;
    }

    /**
     * 
     * @param platform
     *     The platform
     */
    @JsonProperty("platform")
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 
     * @return
     *     The presenceChangedAt
     */
    @JsonProperty("presenceChangedAt")
    public String getPresenceChangedAt() {
        return presenceChangedAt;
    }

    /**
     * 
     * @param presenceChangedAt
     *     The presenceChangedAt
     */
    @JsonProperty("presenceChangedAt")
    public void setPresenceChangedAt(String presenceChangedAt) {
        this.presenceChangedAt = presenceChangedAt;
    }

    /**
     * 
     * @return
     *     The present
     */
    @JsonProperty("present")
    public Boolean getPresent() {
        return present;
    }

    /**
     * 
     * @param present
     *     The present
     */
    @JsonProperty("present")
    public void setPresent(Boolean present) {
        this.present = present;
    }

    /**
     * 
     * @return
     *     The product
     */
    @JsonProperty("product")
    public String getProduct() {
        return product;
    }

    /**
     * 
     * @param product
     *     The product
     */
    @JsonProperty("product")
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * 
     * @return
     *     The provider
     */
    @JsonProperty("provider")
    public Provider getProvider() {
        return provider;
    }

    /**
     * 
     * @param provider
     *     The provider
     */
    @JsonProperty("provider")
    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    /**
     * 
     * @return
     *     The ready
     */
    @JsonProperty("ready")
    public Boolean getReady() {
        return ready;
    }

    /**
     * 
     * @param ready
     *     The ready
     */
    @JsonProperty("ready")
    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    /**
     * 
     * @return
     *     The remoteConnectUrl
     */
    @JsonProperty("remoteConnectUrl")
    public Object getRemoteConnectUrl() {
        return remoteConnectUrl;
    }

    /**
     * 
     * @param remoteConnectUrl
     *     The remoteConnectUrl
     */
    @JsonProperty("remoteConnectUrl")
    public void setRemoteConnectUrl(Object remoteConnectUrl) {
        this.remoteConnectUrl = remoteConnectUrl;
    }

    /**
     * 
     * @return
     *     The remoteConnect
     */
    @JsonProperty("remoteConnect")
    public Boolean getRemoteConnect() {
        return remoteConnect;
    }

    /**
     * 
     * @param remoteConnect
     *     The remoteConnect
     */
    @JsonProperty("remoteConnect")
    public void setRemoteConnect(Boolean remoteConnect) {
        this.remoteConnect = remoteConnect;
    }

    /**
     * 
     * @return
     *     The reverseForwards
     */
    @JsonProperty("reverseForwards")
    public List<Object> getReverseForwards() {
        return reverseForwards;
    }

    /**
     * 
     * @param reverseForwards
     *     The reverseForwards
     */
    @JsonProperty("reverseForwards")
    public void setReverseForwards(List<Object> reverseForwards) {
        this.reverseForwards = reverseForwards;
    }

    /**
     * 
     * @return
     *     The sdk
     */
    @JsonProperty("sdk")
    public String getSdk() {
        return sdk;
    }

    /**
     * 
     * @param sdk
     *     The sdk
     */
    @JsonProperty("sdk")
    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    /**
     * 
     * @return
     *     The serial
     */
    @JsonProperty("serial")
    public String getSerial() {
        return serial;
    }

    /**
     * 
     * @param serial
     *     The serial
     */
    @JsonProperty("serial")
    public void setSerial(String serial) {
        this.serial = serial;
    }

    /**
     * 
     * @return
     *     The statusChangedAt
     */
    @JsonProperty("statusChangedAt")
    public String getStatusChangedAt() {
        return statusChangedAt;
    }

    /**
     * 
     * @param statusChangedAt
     *     The statusChangedAt
     */
    @JsonProperty("statusChangedAt")
    public void setStatusChangedAt(String statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
    }

    /**
     * 
     * @return
     *     The status
     */
    @JsonProperty("status")
    public Double getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(Double status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The using
     */
    @JsonProperty("using")
    public Boolean getUsing() {
        return using;
    }

    /**
     * 
     * @param using
     *     The using
     */
    @JsonProperty("using")
    public void setUsing(Boolean using) {
        this.using = using;
    }

    /**
     * 
     * @return
     *     The version
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * 
     * @param version
     *     The version
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }
    
    /**
     * 
     * @return
     *     The deviceType
     */
    @JsonProperty("deviceType")
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * 
     * @param deviceType
     *     The device type
     */
    @JsonProperty("deviceType")
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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
