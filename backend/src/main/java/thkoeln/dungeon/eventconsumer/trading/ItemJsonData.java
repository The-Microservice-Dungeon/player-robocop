package thkoeln.dungeon.eventconsumer.trading;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import javax.persistence.Embeddable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "rocket",
        "wormhole",
        "longRangeBombardement",
        "selfDestruction",
        "repairSwarm",
        "nuke"
})
@Generated("jsonschema2pojo")
@Embeddable
public class ItemJsonData {

    @JsonProperty("rocket")
    private Integer rocket;
    @JsonProperty("wormhole")
    private Integer wormhole;
    @JsonProperty("longRangeBombardment")
    private Integer longRangeBombardment;
    @JsonProperty("selfDestruction")
    private Integer selfDestruction;
    @JsonProperty("repairSwarm")
    private Integer repairSwarm;
    @JsonProperty("nuke")
    private Integer nuke;

    @JsonProperty("rocket")
    public Integer getRocket() {
        return rocket;
    }

    @JsonProperty("rocket")
    public void setRocket(Integer rocket) {
        this.rocket = rocket;
    }

    @JsonProperty("wormhole")
    public Integer getWormhole() {
        return wormhole;
    }

    @JsonProperty("wormhole")
    public void setWormhole(Integer wormhole) {
        this.wormhole = wormhole;
    }

    @JsonProperty("longRangeBombardment")
    public Integer getLongRangeBombardment() {
        return longRangeBombardment;
    }

    @JsonProperty("longRangeBombardment")
    public void setLongRangeBombardment(Integer longRangeBombardement) {
        this.longRangeBombardment = longRangeBombardement;
    }

    @JsonProperty("selfDestruction")
    public Integer getSelfDestruction() {
        return selfDestruction;
    }

    @JsonProperty("selfDestruction")
    public void setSelfDestruction(Integer selfDestruction) {
        this.selfDestruction = selfDestruction;
    }

    @JsonProperty("repairSwarm")
    public Integer getRepairSwarm() {
        return repairSwarm;
    }

    @JsonProperty("repairSwarm")
    public void setRepairSwarm(Integer repairSwarm) {
        this.repairSwarm = repairSwarm;
    }

    @JsonProperty("nuke")
    public Integer getNuke() {
        return nuke;
    }

    @JsonProperty("nuke")
    public void setNuke(Integer nuke) {
        this.nuke = nuke;
    }

}
