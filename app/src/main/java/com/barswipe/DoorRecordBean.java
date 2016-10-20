package com.barswipe;

import java.io.Serializable;

/**
 * Created by soli on 11/25/15.
 */
public class DoorRecordBean implements Serializable {

    private long OrderID;
    private long DoorID;
    private String OpenDoorTime;

    public long getOrderID() {
        return OrderID;
    }

    public void setOrderID(long orderID) {
        OrderID = orderID;
    }

    public long getDoorID() {
        return DoorID;
    }

    public void setDoorID(long doorID) {
        DoorID = doorID;
    }

    public String getOpenDoorTime() {
        return OpenDoorTime;
    }

    public void setOpenDoorTime(String openDoorTime) {
        OpenDoorTime = openDoorTime;
    }
}
