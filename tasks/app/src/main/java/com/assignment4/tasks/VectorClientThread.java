package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                clientSocket.receive(receivePacket); // receive the message from the server

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println(response);
                // check if the user wants to see the history and send the messages continuously
                if (response.startsWith("history")) {
                    System.out.println("Receiving chat history...");
                    System.out.println(response.replaceFirst("history", "").trim());
                }

                // Parse the response
                String[] responseMessageArray = response.split(":");
                if (responseMessageArray.length < 2) {
                    System.err.println("Invalid response format.");
                    continue; // Skip this iteration on format error
                }

                String responseMessage = responseMessageArray[0];
                String clockString = responseMessageArray[1].trim();

                // Extract vector clock entries
                Pattern p = Pattern.compile("(\\d+)=(\\d+)");
                Matcher m = p.matcher(clockString);

                while (m.find()) {
                    int index = Integer.parseInt(m.group(1));
                    int value = Integer.parseInt(m.group(2));
                    if (index >= 0 && index < vcl.getSize()) {
                        vcl.setVectorClock(index, value);
                    }
                }

                // Update and increment the client's vector clock
                vcl.tick(id);

                System.out.println("Server: " + responseMessage + " : " + vcl.showClock());
            }
        } catch (IOException e) {
            System.err.println("An IOException in VectorClientThread: " + e.getMessage());
        }
    }
}

