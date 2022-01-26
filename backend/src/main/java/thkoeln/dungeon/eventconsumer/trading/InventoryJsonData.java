package thkoeln.dungeon.eventconsumer.trading;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import javax.persistence.Embeddable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "maxStorage",
        "usedStorage",
        "storedCoal",
        "storedIron",
        "storedGem",
        "storedGold",
        "storedPlatin"
})
@Generated("jsonschema2pojo")
@Embeddable
public class InventoryJsonData {

    @JsonProperty("maxStorage")
    private Integer maxStorage;
    @JsonProperty("usedStorage")
    private Integer usedStorage;
    @JsonProperty("storedCoal")
    private Integer storedCoal;
    @JsonProperty("storedIron")
    private Integer storedIron;
    @JsonProperty("storedGem")
    private Integer storedGem;
    @JsonProperty("storedGold")
    private Integer storedGold;
    @JsonProperty("storedPlatin")
    private Integer storedPlatin;

    @JsonProperty("maxStorage")
    public Integer getMaxStorage() {
        return maxStorage;
    }

    @JsonProperty("maxStorage")
    public void setMaxStorage(Integer maxStorage) {
        this.maxStorage = maxStorage;
    }

    @JsonProperty("usedStorage")
    public Integer getUsedStorage() {
        return usedStorage;
    }

    @JsonProperty("usedStorage")
    public void setUsedStorage(Integer usedStorage) {
        this.usedStorage = usedStorage;
    }

    @JsonProperty("storedCoal")
    public Integer getStoredCoal() {
        return storedCoal;
    }

    @JsonProperty("storedCoal")
    public void setStoredCoal(Integer storedCoal) {
        this.storedCoal = storedCoal;
    }

    @JsonProperty("storedIron")
    public Integer getStoredIron() {
        return storedIron;
    }

    @JsonProperty("storedIron")
    public void setStoredIron(Integer storedIron) {
        this.storedIron = storedIron;
    }

    @JsonProperty("storedGem")
    public Integer getStoredGem() {
        return storedGem;
    }

    @JsonProperty("storedGem")
    public void setStoredGem(Integer storedGem) {
        this.storedGem = storedGem;
    }

    @JsonProperty("storedGold")
    public Integer getStoredGold() {
        return storedGold;
    }

    @JsonProperty("storedGold")
    public void setStoredGold(Integer storedGold) {
        this.storedGold = storedGold;
    }

    @JsonProperty("storedPlatin")
    public Integer getStoredPlatin() {
        return storedPlatin;
    }

    @JsonProperty("storedPlatin")
    public void setStoredPlatin(Integer storedPlatin) {
        this.storedPlatin = storedPlatin;
    }

}
