
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
    "developer",
    "id",
    "name",
    "selected",
    "system",
    "type"
})
public class App {

    /**
     * 
     */
    @JsonProperty("developer")
    private String developer;
    /**
     * 
     */
    @JsonProperty("id")
    private String id;
    /**
     * 
     */
    @JsonProperty("name")
    private String name;
    /**
     * 
     */
    @JsonProperty("selected")
    private Boolean selected;
    /**
     * 
     */
    @JsonProperty("system")
    private Boolean system;
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
     *     The developer
     */
    @JsonProperty("developer")
    public String getDeveloper() {
        return developer;
    }

    /**
     * 
     * @param developer
     *     The developer
     */
    @JsonProperty("developer")
    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
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

    /**
     * 
     * @return
     *     The system
     */
    @JsonProperty("system")
    public Boolean getSystem() {
        return system;
    }

    /**
     * 
     * @param system
     *     The system
     */
    @JsonProperty("system")
    public void setSystem(Boolean system) {
        this.system = system;
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
