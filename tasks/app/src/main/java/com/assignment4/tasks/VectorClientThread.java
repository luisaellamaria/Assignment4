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
        while (true) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if (response.startsWith("history")) {
                    System.out.println("Receiving chat history...");
                    System.out.println(response.replaceFirst("history", "").trim());
                } else {
                    int delimiterIndex = response.indexOf(":");
                    if (delimiterIndex == -1) {
                        System.err.println("Received an invalid message format: " + response);
                        continue; // Skip processing if the format is invalid
                    }

                    String serverMessage = response.substring(0, delimiterIndex).trim();
                    String vectorClockData = response.substring(delimiterIndex + 1).trim();

                    // Remove brackets and split the vector clock into key-value pairs
                    vectorClockData = vectorClockData.replaceAll("[\\{\\}]", ""); // Remove curly brackets
                    String[] keyValuePairs = vectorClockData.split(",");

                    for (String pair : keyValuePairs) {
                        String[] entry = pair.split("=");
                        try {
                            int clockIndex = Integer.parseInt(entry[0].trim());
                            int serverTime = Integer.parseInt(entry[1].trim());
                            vcl.setVectorClock(clockIndex, Math.max(vcl.getCurrentTimestamp(clockIndex), serverTime));
                        } catch (NumberFormatException e) {
                            System.err.println("Failed to parse clock value: " + pair);
                            // Handle parse error, possibly continue to the next pair
                        }
                    }

                    vcl.tick(id);
                    System.out.println(serverMessage + ": " + vcl.showClock());
                }
            } catch (IOException e) {
                System.err.println("An I/O error occurred: " + e.getMessage());
                break; // Exit the loop on IOException
            }
        }
    }






}
