package thkoeln.dungeon.eventconsumer.trading;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.List;


@Getter
@Entity
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.NONE)
public class TradingEvent extends AbstractEvent {
    @JsonProperty("data")
    @Embedded
    private TradingData data = null;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("moneyChangedBy")
    private Integer moneyChangedBy;
    @JsonProperty("message")
    private String message;

    @JsonProperty("success")
    public Boolean getSuccess() {
        if (this.success == null) return false;
        return success;
    }

    @JsonProperty("success")
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @JsonProperty("moneyChangedBy")
    public Integer getMoneyChangedBy() {
        return moneyChangedBy;
    }

    @JsonProperty("moneyChangedBy")
    public void setMoneyChangedBy(Integer moneyChangedBy) {
        this.moneyChangedBy = moneyChangedBy;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("data")
    public TradingData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<TradingData> dataList) {
        if (dataList != null) this.data = dataList.get(0);
    }

}
