package com.barswipe;

import java.io.Serializable;

/**
 * Created by soli on 11/25/15.
 */
public class DoorRecordBean implements Serializable {

    private long OrderID;
    private long DoorID;
    private String OpenDoorTime;

    public void setOrderID(long orderID) {
        OrderID = orderID;
    }

    public void setDoorID(long doorID) {
        DoorID = doorID;
    }

    public void setOpenDoorTime(String openDoorTime) {
        OpenDoorTime = openDoorTime;
    }

    public long getOrderID() {
        return OrderID;
    }

    public long getDoorID() {
        return DoorID;
    }

    public String getOpenDoorTime() {
        return OpenDoorTime;
    }
}
