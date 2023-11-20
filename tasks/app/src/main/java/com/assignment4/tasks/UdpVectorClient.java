package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.*;

public class UdpVectorClient {

    public static void main(String[] args) throws Exception {
        System.out.println("Enter your id (1 to 3): ");
        Scanner id_input = new Scanner(System.in);
        int id = id_input.nextInt();

        // prepare the client socket
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        int port = 4040;

        int startTime = 0;
        VectorClock vcl = new VectorClock(4);
        vcl.setVectorClock(id, startTime);

        System.out.println(id + ": Enter any message:");
        Scanner input = new Scanner(System.in);

        while (true) {
            String messageBody = input.nextLine();

            // Increment clock
            if (!messageBody.isEmpty()) {
                vcl.tick(id);
            }

            HashMap<Integer, Integer> messageTime = new HashMap<>();
            messageTime.put(id, vcl.getCurrentTimestamp(id));
            Message msg = new Message(messageBody, messageTime);
            String responseMessage = msg.content + ':' + msg.messageTime;

            // Check if the user wants to quit
            if (messageBody.equals("quit")) {
                clientSocket.close();
                System.exit(1);
            }

            // Send the message to the server
            sendData = responseMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(sendPacket);

            // Check if the user wants to see the history
            if (messageBody.equals("history")) {
                List<String> logs = new ArrayList<>();
                receiveHistory(clientSocket, logs);
                showHistory(logs);
                showSortedHistory(logs);
            } else {
                VectorClientThread client = new VectorClientThread(clientSocket, vcl, id);
                Thread receiverThread = new Thread(client);
                receiverThread.start();
            }
        }
    }

    private static void receiveHistory(DatagramSocket clientSocket, List<String> logs) throws IOException {
        byte[] receiveData = new byte[1024];
        boolean receiving = true;
        clientSocket.setSoTimeout(5000); // 5 seconds timeout for example

        while (receiving) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String log = new String(receivePacket.getData(), 0, receivePacket.getLength());
                logs.add(log);
            } catch (SocketTimeoutException e) {
                receiving = false; // Timeout occurred, stop receiving
            }
        }
    }

    public static void showHistory(List<String> logs) {
        for (String message : logs) {
            System.out.println(message);
        }
    }

    public static void showSortedHistory(List<String> logs) {
        TreeMap<int[], String> sortedLogs = new TreeMap<>(new VectorClockComparator());
        for (String log : logs) {
            String[] parts = log.split(":");
            String message = parts[0];
            int[] clock = Arrays.stream(parts[1].replaceAll("\\[|\\]", "").split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            sortedLogs.put(clock, message);
        }

        for (String message : sortedLogs.values()) {
            System.out.println(message);
        }
    }

    static class VectorClockComparator implements Comparator<int[]> {
        @Override
        public int compare(int[] clock1, int[] clock2) {
            for (int i = 0; i < Math.min(clock1.length, clock2.length); i++) {
                if (clock1[i] != clock2[i]) {
                    return clock1[i] - clock2[i];
                }
            }
            return clock1.length - clock2.length;
        }
    }
}
