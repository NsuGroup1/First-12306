package com.example.a12306f.a;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Order implements Serializable {
    private String id;
    private Passenger[] passengerList;
    private Train train;
    private int status;
    private Date orderTime;
    private double orderPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Passenger[] getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(Passenger[] passengerList) {
        this.passengerList = passengerList;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }


    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", passengerList=" + Arrays.toString(passengerList) +
                ", train=" + train +
                ", status=" + status +
                ", orderTime=" + orderTime +
                ", orderPrice=" + orderPrice +
                '}';
    }
}
