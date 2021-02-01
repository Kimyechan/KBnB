package com.buildup.kbnb.dto;

import java.time.LocalDate;

public class Reservation_Detail_Response {
    private Long roomId;
    private String[] roomImageList;
    private String hostName;
    private String hostImage;
    private int totalCost;
    private int guestNum;
    private String address;
    private double latitude;
    private double longitude;
    private String roomName;
    private int bedRoomNum;
    private int bedNum;
    private int bathRoomNum;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private boolean isSmoking;
    private boolean isParking;
}
