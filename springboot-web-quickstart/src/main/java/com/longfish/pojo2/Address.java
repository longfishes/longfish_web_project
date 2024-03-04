package com.longfish.pojo2;

public class Address {
    private String prov;
    private String city;

    @Override
    public String toString() {
        return "Address{" +
                "prov='" + prov + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
