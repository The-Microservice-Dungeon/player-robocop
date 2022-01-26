package thkoeln.dungeon.eventconsumer.trading;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.UUID;

/**
 * This class is just for the trading event concerning robot buys
 */

@Setter
@Getter
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused") //höhö
public class TradingData {
    @JsonProperty("id")
    private UUID robotId;
    @JsonProperty("player")
    private UUID player;
    @JsonProperty("planet")
    private UUID planet;
    @JsonProperty("alive")
    private Boolean alive;
    @JsonProperty("maxHealth")
    private Integer maxHealth;
    @JsonProperty("maxEnergy")
    private Integer maxEnergy;
    @JsonProperty("energyRegen")
    private Integer energyRegen;
    @JsonProperty("attackDamage")
    private Integer attackDamage;
    @JsonProperty("miningSpeed")
    private Integer miningSpeed;
    @JsonProperty("health")
    private Integer health;
    @JsonProperty("energy")
    private Integer energy;
    @JsonProperty("healthLevel")
    private Integer healthLevel;
    @JsonProperty("damageLevel")
    private Integer damageLevel;
    @JsonProperty("miningSpeedLevel")
    private Integer miningSpeedLevel;
    @JsonProperty("miningLevel")
    private Integer miningLevel;
    @JsonProperty("energyLevel")
    private Integer energyLevel;
    @JsonProperty("energyRegenLevel")
    private Integer energyRegenLevel;
    @JsonProperty("storageLevel")
    private Integer storageLevel;
    @JsonProperty("inventory")
    @Embedded
    private InventoryJsonData inventoryJsonData;
    @JsonProperty("items")
    @Embedded
    private ItemJsonData itemJsonData;

    @JsonProperty("id")
    public UUID getRobotId() {
        return robotId;
    }

    @JsonProperty("id")
    public void setRobotId(String id) {
        this.robotId = UUID.fromString(id);
    }

    @JsonProperty("player")
    public UUID getPlayer() {
        return player;
    }

    @JsonProperty("player")
    public void setPlayer(String player) {
        this.player = UUID.fromString(player);
    }

    @JsonProperty("planet")
    public UUID getPlanet() {
        return planet;
    }

    @JsonProperty("planet")
    public void setPlanet(String planet) {
        this.planet = UUID.fromString(planet);
    }

    @JsonProperty("alive")
    public Boolean getAlive() {
        return alive;
    }

    @JsonProperty("alive")
    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    @JsonProperty("maxHealth")
    public Integer getMaxHealth() {
        return maxHealth;
    }

    @JsonProperty("maxHealth")
    public void setMaxHealth(Integer maxHealth) {
        this.maxHealth = maxHealth;
    }

    @JsonProperty("maxEnergy")
    public Integer getMaxEnergy() {
        return maxEnergy;
    }

    @JsonProperty("maxEnergy")
    public void setMaxEnergy(Integer maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @JsonProperty("energyRegen")
    public Integer getEnergyRegen() {
        return energyRegen;
    }

    @JsonProperty("energyRegen")
    public void setEnergyRegen(Integer energyRegen) {
        this.energyRegen = energyRegen;
    }

    @JsonProperty("attackDamage")
    public Integer getAttackDamage() {
        return attackDamage;
    }

    @JsonProperty("attackDamage")
    public void setAttackDamage(Integer attackDamage) {
        this.attackDamage = attackDamage;
    }

    @JsonProperty("miningSpeed")
    public Integer getMiningSpeed() {
        return miningSpeed;
    }

    @JsonProperty("miningSpeed")
    public void setMiningSpeed(Integer miningSpeed) {
        this.miningSpeed = miningSpeed;
    }

    @JsonProperty("health")
    public Integer getHealth() {
        return health;
    }

    @JsonProperty("health")
    public void setHealth(Integer health) {
        this.health = health;
    }

    @JsonProperty("energy")
    public Integer getEnergy() {
        return energy;
    }

    @JsonProperty("energy")
    public void setEnergy(Integer energy) {
        this.energy = energy;
    }

    @JsonProperty("healthLevel")
    public Integer getHealthLevel() {
        return healthLevel;
    }

    @JsonProperty("healthLevel")
    public void setHealthLevel(Integer healthLevel) {
        this.healthLevel = healthLevel;
    }

    @JsonProperty("damageLevel")
    public Integer getDamageLevel() {
        return damageLevel;
    }

    @JsonProperty("damageLevel")
    public void setDamageLevel(Integer damageLevel) {
        this.damageLevel = damageLevel;
    }

    @JsonProperty("miningSpeedLevel")
    public Integer getMiningSpeedLevel() {
        return miningSpeedLevel;
    }

    @JsonProperty("miningSpeedLevel")
    public void setMiningSpeedLevel(Integer miningSpeedLevel) {
        this.miningSpeedLevel = miningSpeedLevel;
    }

    @JsonProperty("miningLevel")
    public Integer getMiningLevel() {
        return miningLevel;
    }

    @JsonProperty("miningLevel")
    public void setMiningLevel(Integer miningLevel) {
        this.miningLevel = miningLevel;
    }

    @JsonProperty("energyLevel")
    public Integer getEnergyLevel() {
        return energyLevel;
    }

    @JsonProperty("energyLevel")
    public void setEnergyLevel(Integer energyLevel) {
        this.energyLevel = energyLevel;
    }

    @JsonProperty("energyRegenLevel")
    public Integer getEnergyRegenLevel() {
        return energyRegenLevel;
    }

    @JsonProperty("energyRegenLevel")
    public void setEnergyRegenLevel(Integer energyRegenLevel) {
        this.energyRegenLevel = energyRegenLevel;
    }

    @JsonProperty("storageLevel")
    public Integer getStorageLevel() {
        return storageLevel;
    }

    @JsonProperty("storageLevel")
    public void setStorageLevel(Integer storageLevel) {
        this.storageLevel = storageLevel;
    }

    @JsonProperty("inventory")
    public InventoryJsonData getInventoryJsonData() {
        return inventoryJsonData;
    }

    @JsonProperty("inventory")
    public void setInventoryJsonData(InventoryJsonData inventoryJsonData) {
        this.inventoryJsonData = inventoryJsonData;
    }

    @JsonProperty("items")
    public ItemJsonData getItemJsonData() {
        return itemJsonData;
    }

    @JsonProperty("items")
    public void setItemJsonData(ItemJsonData itemJsonData) {
        this.itemJsonData = itemJsonData;
    }

}
