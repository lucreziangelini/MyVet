package it.ispwproject.myvet.bean;

import java.util.Map;

public class StatisticsBean {

    private int totalBookings;
    private int cancelledBookings;
    private double cancellationRate;
    private Map<String, Integer> topVeterinarians;

    public StatisticsBean(int totalBookings,
                          int cancelledBookings,
                          double cancellationRate,
                          Map<String, Integer> topVeterinarians) {
        this.totalBookings = totalBookings;
        this.cancelledBookings = cancelledBookings;
        this.cancellationRate = cancellationRate;
        this.topVeterinarians = topVeterinarians;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public int getCancelledBookings() {
        return cancelledBookings;
    }

    public double getCancellationRate() {
        return cancellationRate;
    }

    public Map<String, Integer> getTopVeterinarians() {
        return topVeterinarians;
    }
}