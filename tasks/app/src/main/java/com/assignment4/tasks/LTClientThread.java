package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LTClientThread implements Runnable {

    private final DatagramSocket clientSocket;
    private LamportTimestamp lc;

    private byte[] receiveData = new byte[1024];

    public LTClientThread(DatagramSocket clientSocket, LamportTimestamp lc) {
        this.clientSocket = clientSocket;
        this.lc = lc;
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                // Receive packet from server
                clientSocket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // Parse the response
                String[] parts = response.split(":");
                if (parts.length >= 2) {
                    String message = parts[0];
                    int receivedTimestamp = Integer.parseInt(parts[1]);

                    // Update the Lamport clock (only if the received timestamp is greater than the clock value for the client)
                    lc.updateClock(receivedTimestamp);

                    // Print the message along with the updated timestamp
                    System.out.println("Server: " + message + " : " + lc.getCurrentTimestamp());
                }
            } catch (IOException e) {
                System.err.println("IOException in client thread: " + e.getMessage());
                break;
            }
        }
    }
}
