
package com.assignment4.tasks;


public class LamportTimestamp {
    // implement the Lamport logical clock by first defining the timestamp variable
    private int timestamp;
    public LamportTimestamp(int time){

        // initialize the timestamp with time parameter
        timestamp = time;
    }
    public void tick(){
        // update the timestamp by 1
        timestamp++;
    }
    public int getCurrentTimestamp(){
        // returns the current timestamp
        return timestamp;
    }
    public void updateClock(int receivedTimestamp){
        // Update the timestamp to the maximum of the current and the received timestamp
        timestamp = Math.max(timestamp, receivedTimestamp);
        // Increment the timestamp to ensure uniqueness
        timestamp++;
    }
}
