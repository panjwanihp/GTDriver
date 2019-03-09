package piktoclean.com.gtdriver;

public class Point {
    double lat,lon;
    Point(double lat,double lon){
        this.lat=lat;
        this.lon=lon;

    }
    double getLat(){ return lat;};
    double getLon(){ return lon;};
}
