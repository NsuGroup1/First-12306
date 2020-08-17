package com.example.a12306f.a;

import java.io.Serializable;

public class Seat implements Serializable {
    private String seatName;
    private int seatNum;
    private Double seatPrice;
    private String seatNO;

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public Double getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(Double seatPrice) {
        this.seatPrice = seatPrice;
    }

    public String getSeatNO() {
        return seatNO;
    }

    public void setSeatNO(String seatNO) {
        this.seatNO = seatNO;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatName='" + seatName + '\'' +
                ", seatNum=" + seatNum +
                ", seatPrice=" + seatPrice +
                ", seatNO='" + seatNO + '\'' +
                '}';
    }
}
