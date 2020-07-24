package ch.jmt.riderhub;

public class Rideout {

    private String title;
    private int day;
    private int month;
    private int year;

    // route from maps sdk

    public Rideout(String title, int day, int month, int year) {
        this.title = title;
        this.day = day;
        this.month = month;
        this.year = year;
    }
}
