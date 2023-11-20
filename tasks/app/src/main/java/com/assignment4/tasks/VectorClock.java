package com.assignment4.tasks;

import java.util.Arrays;

public class VectorClock {

    private final int[] timestamps;

    public VectorClock(int numOfClients) {
        timestamps = new int[numOfClients];
        Arrays.fill(timestamps, 0);
    }

    public void setVectorClock(int processId, int time) {
        timestamps[processId] = time;
    }

    public void tick(int processId) {
        timestamps[processId]++; // Increment the clock for the given process id
    }

    public int getCurrentTimestamp(int processId) {
        return timestamps[processId];
    }

    public void updateClock(VectorClock other) {
        for (int i = 0; i < timestamps.length; i++) {
            timestamps[i] = Math.max(timestamps[i], other.timestamps[i]);
        }
    }

    public String showClock() {
        return Arrays.toString(timestamps);
    }
}
