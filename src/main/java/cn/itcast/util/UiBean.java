package cn.itcast.util;
import java.util.Arrays;
public class UiBean {
    private String [] produceId;
    private String [] producetSumPrice;
    public UiBean() {
    }
    public UiBean(String[] produceId, String[] producetSumPrice) {
        this.produceId = produceId;
        this.producetSumPrice = producetSumPrice;
    }
    public String[] getProduceId() {
        return produceId;
    }
    public void setProduceId(String[] produceId) {
        this.produceId = produceId;
    }
    public String[] getProducetSumPrice() {
        return producetSumPrice;
    }
    public void setProducetSumPrice(String[] producetSumPrice) {
        this.producetSumPrice = producetSumPrice;
    }
    @Override
    public String toString() {
        return "UiBean{" +
                "produceId=" + Arrays.toString(produceId) +
                ", producetSumPrice=" + Arrays.toString(producetSumPrice) +
                '}';
    } }