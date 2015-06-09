package org.opendoors;

/**
 * Created by Ruben on 9-6-2015.
 */
public class Coordinates {
    private float lat;
    private float lng;

    public Coordinates(float lat, float lng) {
        assert lat < 90: "Latitude too big";
        assert lng < 180: "Longitude too big";

        assert lat > -90: "Latitude too small";
        assert lng > -180: "Longitude too small";

        this.lat = lat;
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }
}
