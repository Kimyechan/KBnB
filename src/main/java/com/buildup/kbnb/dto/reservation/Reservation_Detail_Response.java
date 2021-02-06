package com.buildup.kbnb.dto.reservation;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation_Detail_Response {
    private Long roomId;
    private List<String> roomImageList;
    private String hostName;
    private String hostImage;
    private Long totalCost;
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
