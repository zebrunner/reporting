
package com.qaprosoft.zafira.dbaccess.model.stf;

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
    "apps",
    "selected"
})
public class Browser {

    /**
     * 
     */
    @JsonProperty("apps")
    private List<App> apps = new ArrayList<App>();
    /**
     * 
     */
    @JsonProperty("selected")
    private Boolean selected;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The apps
     */
    @JsonProperty("apps")
    public List<App> getApps() {
        return apps;
    }

    /**
     * 
     * @param apps
     *     The apps
     */
    @JsonProperty("apps")
    public void setApps(List<App> apps) {
        this.apps = apps;
    }

    /**
     * 
     * @return
     *     The selected
     */
    @JsonProperty("selected")
    public Boolean getSelected() {
        return selected;
    }

    /**
     * 
     * @param selected
     *     The selected
     */
    @JsonProperty("selected")
    public void setSelected(Boolean selected) {
        this.selected = selected;
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
