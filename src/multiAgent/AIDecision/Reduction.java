package multiAgent.AIDecision;

/**
 * Created by H77 on 2017/7/24.
 */
public class Reduction {


    int reduction;
    double maxReduction;
    double minReduction;
    boolean isOn;
    int current_Price;

    //空房率基准
    double vacancy;
    double minDegree;
    double maxDegree;
    //是否降价
    boolean isReduction;

    //是否参考以前的价格
    boolean isPrePrice;
    double prePrice;

    boolean hotEvent = false;

    public Reduction(int reduction, double maxReduction, double minReduction, boolean isOn) {
        this.reduction = reduction;
        this.maxReduction = maxReduction;
        this.minReduction = minReduction;
        this.isOn = isOn;
    }

    public Reduction(){

    }

    public int getReduction() {
        return reduction;
    }

    public void setReduction(int reduction) {
        this.reduction = reduction;
    }

    public double getMaxReduction() {
        return maxReduction;
    }

    public void setMaxReduction(double maxReduction) {
        this.maxReduction = maxReduction;
    }

    public double getMinReduction() {
        return minReduction;
    }

    public void setMinReduction(double minReduction) {
        this.minReduction = minReduction;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
    public int getCurrent_Price() {
        return current_Price;
    }

    public void setCurrent_Price(int current_Price) {
        this.current_Price = current_Price;
    }
    public double getVacancy() {
        return vacancy;
    }

    public void setVacancy(double vacancy) {
        this.vacancy = vacancy;
    }
    public double getMinDegree() {
        return minDegree;
    }

    public void setMinDegree(double minDegree) {
        this.minDegree = minDegree;
    }

    public double getMaxDegree() {
        return maxDegree;
    }

    public void setMaxDegree(double maxDegree) {
        this.maxDegree = maxDegree;
    }

    public boolean isReduction() {
        return isReduction;
    }

    public void setReduction(boolean reduction) {
        isReduction = reduction;
    }

    public boolean isPrePrice() {
        return isPrePrice;
    }

    public void setPrePrice(boolean prePrice) {
        isPrePrice = prePrice;
    }

    public double getPrePrice() {
        return prePrice;
    }

    public void setPrePrice(double prePrice) {
        this.prePrice = prePrice;
    }

    public boolean isHotEvent() {
        return hotEvent;
    }

    public void setHotEvent(boolean hotEvent) {
        this.hotEvent = hotEvent;
    }
}
