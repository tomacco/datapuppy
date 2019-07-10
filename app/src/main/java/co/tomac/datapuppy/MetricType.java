package co.tomac.datapuppy;

public enum MetricType {
    CPU,
    RAM,
    BATTERY;

    public String getStatus() {
        return this.name();
    }
}
