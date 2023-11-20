package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class VectorClientThread implements Runnable {

    private final DatagramSocket clientSocket;
    VectorClock vcl;
    byte[] receiveData = new byte[1024];

    int id;
    public VectorClientThread(DatagramSocket clientSocket, VectorClock vcl, int id) {

        this.clientSocket = clientSocket;
        this.vcl = vcl;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // New code for splitting the response and filtering out timestamps
                String[] responseMessageArray = response.replaceAll("\\p{Punct}", " ").trim().split("\\s+");

                // Assuming the first part of the array is the server message
                String serverMessage = responseMessageArray[0];

                // Extracting vector clock data from the response
                String vectorClockData = ""; // Initialize an empty string for the clock data
                for (int i = 1; i < responseMessageArray.length; i++) {
                    vectorClockData += responseMessageArray[i] + (i < responseMessageArray.length - 1 ? "," : "");
                }

                // Update the vector clock
                String[] clockValues = vectorClockData.split(",");
                for (int i = 0; i < clockValues.length; i++) {
                    int serverTime = Integer.parseInt(clockValues[i].trim());
                    vcl.setVectorClock(i, Math.max(vcl.getCurrentTimestamp(i), serverTime));
                }

                // Increment local clock
                vcl.tick(id);

                System.out.println("Server: " + serverMessage + " " + vcl.showClock());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
