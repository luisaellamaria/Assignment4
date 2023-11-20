package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;


public class UdpVectorClient {

    public static void main(String[] args) throws Exception
    {
        System.out.println("Enter your id (1 to 3): ");
        Scanner id_input = new Scanner(System.in);
        int id = id_input.nextInt();

        // prepare the client socket
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");

        // initialize the buffers
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        int port = 4040;
        List<String> logs;

        int startTime = 0;
        VectorClock vcl = new VectorClock(4);
        vcl.setVectorClock(id, startTime);

        //ask for user input aka message to the server
        System.out.println(id+": Enter any message:");
        Scanner input = new Scanner(System.in);

        while(true) {
            String messageBody = input.nextLine();
            // increment clock
            if (!messageBody.isEmpty()){
                vcl.tick(id);
            }
            HashMap<Integer, Integer> messageTime = new HashMap<>();
            messageTime.put(id,vcl.getCurrentTimestamp(id));
            Message msg = new Message(messageBody, messageTime);
            String responseMessage = msg.content + ':' + msg.messageTime;

            // check if the user wants to quit
            if(messageBody.equals("quit")){
                clientSocket.close();
                System.exit(1);
            }

            // send the message to the server
            sendData = responseMessage.getBytes();

            // send message to the server
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(sendPacket);

            // check if the user wants to see the history
            if(messageBody.equals("history")) {
                System.out.println("Receiving the chat history...");
                logs = new ArrayList<>();

                // recieve chat history
                clientSocket.setSoTimeout(5000); // Set timeout for 5 seconds
                while (true) {
                    try {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);
                        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        logs.add(receivedMessage);
                    } catch (IOException e) {
                        // Timeout reached, break the loop
                        break;
                    }
                }

                UdpVectorClient uc = new UdpVectorClient();
                uc.showHistory(logs); // gives out all the unsorted logs stored at the server
                uc.showSortedHistory(logs); // shows sorted logs
            }
            else
            {
                VectorClientThread client;
                client = new VectorClientThread(clientSocket, vcl, id);
                Thread receiverThread = new Thread(client);
                receiverThread.start();
            }
        }
    }
    public void showHistory(List<String> logs){
        // prints the unsorted logs (history) coming form the server
        for (String message : logs) {
            System.out.println(message);
        }
    }

    public void showSortedHistory(List<String> logs) {
        System.out.println("Print sorted conversation using attached vector clocks");
        Map<List<Integer>, String> logMap = new HashMap<>();

        // Extract vector clocks and messages
        for (String log : logs) {
            String[] parts = log.split(":");
            String message = parts[0];

            // Remove non-numeric characters like brackets and trim spaces
            String clockString = parts[1].replaceAll("[\\[\\]]", "");
            List<Integer> clock;
            try {
                clock = Arrays.stream(clockString.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                System.out.println("Error parsing vector clock: " + e.getMessage());
                continue;
            }

            logMap.put(clock, message);
        }

        // Sort using the custom comparator
        Comparator<List<Integer>> vectorClockComparator = new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> clock1, List<Integer> clock2) {
                int size = Math.min(clock1.size(), clock2.size());
                for (int i = 0; i < size; i++) {
                    int comparison = clock1.get(i).compareTo(clock2.get(i));
                    if (comparison != 0) {
                        return comparison;
                    }
                }
                return Integer.compare(clock1.size(), clock2.size());
            }
        };

        List<Map.Entry<List<Integer>, String>> sortedLogs = new ArrayList<>(logMap.entrySet());
        sortedLogs.sort(Map.Entry.comparingByKey(vectorClockComparator));

        // Print sorted logs
        for (Map.Entry<List<Integer>, String> entry : sortedLogs) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }





}
