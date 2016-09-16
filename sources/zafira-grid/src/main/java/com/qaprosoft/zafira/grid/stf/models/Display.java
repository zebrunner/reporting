
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
    "density",
    "fps",
    "height",
    "id",
    "rotation",
    "secure",
    "size",
    "url",
    "width",
    "xdpi",
    "ydpi"
})
public class Display {

    /**
     * 
     */
    @JsonProperty("density")
    private Double density;
    /**
     * 
     */
    @JsonProperty("fps")
    private Double fps;
    /**
     * 
     */
    @JsonProperty("height")
    private Double height;
    /**
     * 
     */
    @JsonProperty("id")
    private Double id;
    /**
     * 
     */
    @JsonProperty("rotation")
    private Double rotation;
    /**
     * 
     */
    @JsonProperty("secure")
    private Boolean secure;
    /**
     * 
     */
    @JsonProperty("size")
    private Double size;
    /**
     * 
     */
    @JsonProperty("url")
    private String url;
    /**
     * 
     */
    @JsonProperty("width")
    private Double width;
    /**
     * 
     */
    @JsonProperty("xdpi")
    private Double xdpi;
    /**
     * 
     */
    @JsonProperty("ydpi")
    private Double ydpi;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The density
     */
    @JsonProperty("density")
    public Double getDensity() {
        return density;
    }

    /**
     * 
     * @param density
     *     The density
     */
    @JsonProperty("density")
    public void setDensity(Double density) {
        this.density = density;
    }

    /**
     * 
     * @return
     *     The fps
     */
    @JsonProperty("fps")
    public Double getFps() {
        return fps;
    }

    /**
     * 
     * @param fps
     *     The fps
     */
    @JsonProperty("fps")
    public void setFps(Double fps) {
        this.fps = fps;
    }

    /**
     * 
     * @return
     *     The height
     */
    @JsonProperty("height")
    public Double getHeight() {
        return height;
    }

    /**
     * 
     * @param height
     *     The height
     */
    @JsonProperty("height")
    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public Double getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(Double id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The rotation
     */
    @JsonProperty("rotation")
    public Double getRotation() {
        return rotation;
    }

    /**
     * 
     * @param rotation
     *     The rotation
     */
    @JsonProperty("rotation")
    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }

    /**
     * 
     * @return
     *     The secure
     */
    @JsonProperty("secure")
    public Boolean getSecure() {
        return secure;
    }

    /**
     * 
     * @param secure
     *     The secure
     */
    @JsonProperty("secure")
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    /**
     * 
     * @return
     *     The size
     */
    @JsonProperty("size")
    public Double getSize() {
        return size;
    }

    /**
     * 
     * @param size
     *     The size
     */
    @JsonProperty("size")
    public void setSize(Double size) {
        this.size = size;
    }

    /**
     * 
     * @return
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The width
     */
    @JsonProperty("width")
    public Double getWidth() {
        return width;
    }

    /**
     * 
     * @param width
     *     The width
     */
    @JsonProperty("width")
    public void setWidth(Double width) {
        this.width = width;
    }

    /**
     * 
     * @return
     *     The xdpi
     */
    @JsonProperty("xdpi")
    public Double getXdpi() {
        return xdpi;
    }

    /**
     * 
     * @param xdpi
     *     The xdpi
     */
    @JsonProperty("xdpi")
    public void setXdpi(Double xdpi) {
        this.xdpi = xdpi;
    }

    /**
     * 
     * @return
     *     The ydpi
     */
    @JsonProperty("ydpi")
    public Double getYdpi() {
        return ydpi;
    }

    /**
     * 
     * @param ydpi
     *     The ydpi
     */
    @JsonProperty("ydpi")
    public void setYdpi(Double ydpi) {
        this.ydpi = ydpi;
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
