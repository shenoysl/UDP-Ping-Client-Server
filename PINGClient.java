/****
 * Name: Satya Shenoy
 * Date: 4/25/2023
 * Program: PINGClient
 * Class: CMSC440
 * ******/


import java.io.*;
import java.net.*;
import java.util.*;

public class PINGClient {

    public static void main(String args[]) throws Exception
    {

        if (args.length != 5 ) {
            System.out.println ("ERR arg-5: numRequests");
            System.exit (-1);
        }

        

// socket variables
        DatagramSocket clientSocket;
        DatagramPacket sendPacket;
        DatagramPacket receivePacket;
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        InetAddress IPAddress;
// client variables
        String packetInfo, serverSentence, payload, rest;

// command-line arguments
        InetAddress address = InetAddress.getByName(args[0]);
        String host = address.getHostName();


        int port = Integer.parseInt(args[1]);
        int clientID = Integer.parseInt(args[2]);
        int numRequests = Integer.parseInt(args[3]);
        int waitTime = Integer.parseInt(args[4]);

// process command-line arguments


// Create client socket to destination
        clientSocket = new DatagramSocket();

// Print out initial Sentence
        System.out.println("PINGClient started with server IP: " + address + ", port: " + port + ", client ID: " + clientID + ", packets: " + numRequests + ", wait: " + waitTime);

// initialize variables for use later
        int sequenceNum=1;

        double droppedpackets = 0;

        double loss_rate = 0;

        int numResponses = 0;

        int payloadSum = 0;

        double rtt=0;

        double rttSum=0;

        double maxRTT=0;

        double minRTT=1;

        // Loop until the sequence number reaches the number of requests
        for(sequenceNum=1; sequenceNum<numRequests+1;sequenceNum++) {

            
            // get timestamp of when packet is being constructed
            Date now = new Date();
            long time = now.getTime();

            Random rand = new Random();
        int randSize = rand.nextInt(151) + 150;
          // long time = System.currentTimeMillis();
            int version = 1;

            // Initiliaze class-name and user-name Strings
            String class_name = "VCU-CMSC440-SPRING-2023";
            String user_name = "Shenoy, Satya";
          

               
    // Create payload string         
    payload = "Host: " + "\n" + host + "\n" 
               + "Class-name: " + "\n" + class_name + "\n" 
               + "User-name: " + "\n" + user_name;

    // Convert payload string into a byte array             
    byte[] payLoadbytes = payload.getBytes();

    // Get the size of the payload byte array
    int payloadSize = payLoadbytes.length;
              
    // Compute the size of the random Rest string
    int restSize = randSize-payloadSize;

    // Generate the random rest string
    rest = getRandString(restSize/2);

    // Create the full payload string concatenated with the rest string
    String finalPayload = payload + "\n" + "Rest: " + "\n" + rest;

   
    // Construct packet string to send to Server
    packetInfo = "Version: " + "\n" + version + "\n"
             + "Client ID: " + "\n" + clientID + "\n" 
             + "Sequence No.: " + "\n" + sequenceNum + "\n"
              + "Time: " + "\n" + time + "\n"
               + "Payload Size: " + "\n" + randSize + "\n"  + 
               finalPayload;


       
    // Add to the payload (used to compute average payload later)           
    payloadSum += randSize;

    // Convert the packet string into byte array
    sendData = packetInfo.getBytes();

    // Create packet and send to server
            sendPacket = new DatagramPacket(sendData, sendData.length, address, port);

            
            // Print the request packet header and payload
            System.out.println("---------- Ping Request Packet Header ----------");
            System.out.println("Version: " + version);
            System.out.println("Client ID: " + clientID);
            System.out.println("Sequence No.: " + sequenceNum);
            System.out.println("Time: " + time);
            System.out.println("Payload Size: " + randSize);
            System.out.println("--------- Ping Request Packet Payload ------------");
            System.out.println("Host: " + host);
            System.out.println("Class-name: " + class_name);
            System.out.println("User-name: " + user_name);
            System.out.println("Rest: " + rest + "\n");


            // Send the packet to the server
            clientSocket.send(sendPacket);





// Create receiving packet and receive from server



            try {

                // set the client socket to timeout when it reaches the waitTime, if it does, go to catch block
                clientSocket.setSoTimeout(waitTime*1000);

                // create a Datagram packet and a socket to receive from the server
                receivePacket = new DatagramPacket(receiveData, receiveData.length);

                clientSocket.receive(receivePacket);

               // A packet has been received, increment the number of response packets
                numResponses++;

                // Get the time that the packet was received and calculate rtt
                now = new Date();
                long receivedTime = now.getTime();

              
            // calculate RTT
              rtt = receivedTime - time;

              // Continually add rtt values here, (used for calculating average)
              rttSum+=rtt;

              // check to see if RTT is the max
              if(rtt>maxRTT){

                maxRTT=rtt;

              }

              // check to see if RTT is the min 
              if(rtt<minRTT){

                minRTT=rtt;

              }

                // Create a string variable of the data from packet received
                serverSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // Create a string array of the received data, split at \n character
                String[] serverSentenceparts = serverSentence.split("\n");
             
                // parse the Server packet to get each field
                String packet_version = serverSentenceparts[0];
                String packet_clientID = serverSentenceparts[1];
                String packet_sequenceNum = serverSentenceparts[2];
                String packet_timestamp = serverSentenceparts[3];
                String packet_payloadSize = serverSentenceparts[4];
                String packet_hostName = serverSentenceparts[5];
                String packet_class_name = serverSentenceparts[6];
                String packet_user_name = serverSentenceparts[7];
                String packet_rest = serverSentenceparts[8];


            // print out the packet received from server
                System.out.println("---------- Received Ping Response Packet Header ----------");
                System.out.println(packet_version);
                System.out.println(packet_clientID);
                System.out.println(packet_sequenceNum);
                System.out.println(packet_timestamp);
                System.out.println(packet_payloadSize);
                System.out.println("--------- Received Ping Response Packet Payload ------------");
                System.out.println(packet_hostName);
                System.out.println(packet_class_name);
                System.out.println(packet_user_name);
                System.out.println(packet_rest);
                System.out.println("---------------------------------------");
                System.out.println("RTT: " + rtt + "ms " + "\n");
            
            } // If the socket does not receive a packet within the waitTime, catch and display message
            catch (IOException e) {

                System.out.println("\n" + "---------- Ping Response Packet Timed-Out ----------" + "\n");


                droppedpackets++;

            }


        }

        clientSocket.close(); // close the clientSocket


        double lossRate = (double) droppedpackets/numRequests; // compute the loss rate
 

        
     // print out summary statistics           
     System.out.println("Summary: " + numRequests + " :: " + numResponses + " :: " + Math.round(lossRate*100) + "% :: " +  minRTT + " :: " + maxRTT + " :: " + rttSum/numResponses + " :: " + payloadSum/numRequests);
    } // end main


    // method to generate random string
    static String getRandString(int n)
    {

        // choose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

}











