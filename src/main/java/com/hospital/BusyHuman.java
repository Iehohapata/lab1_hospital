package com.hospital;

import java.time.LocalTime;

public abstract class BusyHuman {
    protected boolean timesOverlap(LocalTime start1, LocalTime end1,
            LocalTime start2, LocalTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    public abstract boolean canAcceptAppointment(Appointment candidate);
}
