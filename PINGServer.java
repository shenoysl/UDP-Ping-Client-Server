/****
 * Name: Satya Shenoy
 * Date: 4/25/2023
 * Program: PINGClient
 * Class: CMSC440
 * ******/


import java.io.*;
import java.net.*;
import java.util.*;

public class PINGServer {

    public static void main(String args[]) throws Exception
    {
// socket variables
        DatagramSocket serverSocket;
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        InetAddress IPAddress;
        int clientPort;
// server variables
        String serverSentence;
// command-line arguments
        int port;
        int loss_rate; 

// Ensure user has entered both command-line arguments
        if (args.length !=2){
            System.out.println("Need port and loss_rate\n");
            System.exit(-1);
        }

// Parse the arguments into port and loss integer variables
        port = Integer.parseInt(args[0]);
        loss_rate = Integer.parseInt(args[1]);

 // ensure that the port that the user has entered is valid       
        if (port<1 || port>=65536){
            System.out.println("Invalid Port Number");
            System.exit(-1);
        }

// Create variable to get the IP of the server
        InetAddress serverIP = InetAddress.getLocalHost();


        
        Random random = new Random();


// Create welcoming socket using given port, throw error if the port is being used 
         serverSocket = new DatagramSocket();
        try {
            serverSocket = new DatagramSocket(port);

        }
        catch(IOException e){
            System.out.println("ERR - cannot create PINGServer socket using port number " + port);
        }



       // Send message that PingServer has started 
        System.out.println("PINGServer started with server IP: " + serverIP.getHostAddress() + ", " + port + "... ");


// While loop to handle arbitrary sequence of clients making requests
        while (true) {
// Waits for some client to send a packet
            DatagramPacket receivePacket = new DatagramPacket
                    (receiveData,receiveData.length);
            serverSocket.receive(receivePacket);

            // Convert the data received from client packet, into a string
            String clientSentence = new String(receivePacket.getData(), 0,
                    receivePacket.getLength());

            // Create string array of the received data, split at \n
             String[] clientSentenceparts = clientSentence.split("\n");

             // parse the string array, concatenate each field 
               String  packet_version = clientSentenceparts[0]+clientSentenceparts[1];
                String packet_clientID = clientSentenceparts[2]+clientSentenceparts[3];
                String packet_sequenceNum = clientSentenceparts[4]+clientSentenceparts[5];
               String packet_timestamp = clientSentenceparts[6]+clientSentenceparts[7];
                String packet_payloadSize = clientSentenceparts[8]+clientSentenceparts[9];
                String packet_hostName = clientSentenceparts[10]+clientSentenceparts[11];
                String packet_class_name = clientSentenceparts[12]+clientSentenceparts[13];
                String packet_user_name = clientSentenceparts[14]+clientSentenceparts[15];
                String packet_rest = clientSentenceparts[16]+clientSentenceparts[17];

           
           // generate random integer between 1-100 to compare with loss rate
            int checkLoss = random.nextInt(100) + 1;


            // if the random integer is less than the Loss rate, notify that the packet has been dropped and reiterate the loop
            if(checkLoss<=loss_rate){
               System.out.println("IP:" + receivePacket.getAddress() + ":: Port: " + receivePacket.getPort() + " :  ClientID: " + clientSentenceparts[3] + ":: Seq#: " + clientSentenceparts[5] + ":: DROPPED");
                System.out.println("---------- Received Ping Request Packet Header ----------");
                System.out.println(packet_version);
                System.out.println(packet_clientID);
                System.out.println(packet_sequenceNum);
                System.out.println(packet_timestamp);
                System.out.println(packet_payloadSize);
                System.out.println("--------- Received Ping Request Packet Payload ------------");
                System.out.println(packet_hostName);
                System.out.println(packet_class_name);
                System.out.println(packet_user_name);
                System.out.println(packet_rest);
                System.out.println("---------------------------------------" + "\n");

                continue;
            }


            // If packet is not lost, notify that packet has been received and print the request packet header and payload
            System.out.println("IP:" + receivePacket.getAddress() + ":: Port: " + receivePacket.getPort() + " :  ClientID: " + clientSentenceparts[3] + ":: Seq#: " + clientSentenceparts[5] + ":: RECEIVED");
            System.out.println("---------- Received Ping Request Packet Header ----------");
                System.out.println(packet_version);
                System.out.println(packet_clientID);
                System.out.println(packet_sequenceNum);
                System.out.println(packet_timestamp);
                System.out.println(packet_payloadSize);
                System.out.println("--------- Received Ping Request Packet Payload ------------");
                System.out.println(packet_hostName);
                System.out.println(packet_class_name);
                System.out.println(packet_user_name);
                System.out.println(packet_rest);
            System.out.println("---------------------------------------" + "\n");



            // Create string variables to capitalize the hostname, class-name, user-name and rest strings, and concatenate
              String sendHost = clientSentenceparts[10] + clientSentenceparts[11].toUpperCase();
              String sendClass = clientSentenceparts[12] + clientSentenceparts[13].toUpperCase();
              String sendUser = clientSentenceparts[14] + clientSentenceparts[15].toUpperCase();
              String sendRest = clientSentenceparts[16] + clientSentenceparts[17].toUpperCase();

           
              // Create string to send to client
             String clientMessage = packet_version + "\n" + packet_clientID + "\n" + packet_sequenceNum + "\n" + packet_timestamp + "\n" + packet_payloadSize + "\n" + sendHost + "\n" + sendClass + "\n" + sendUser + "\n" + sendRest;


        // Set the server message String to the client message string
           serverSentence = clientMessage;
// Write output line to socket
            IPAddress = receivePacket.getAddress();
            clientPort = receivePacket.getPort();
            sendData = serverSentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length,
                    IPAddress,
                    clientPort);
            serverSocket.send(sendPacket);

          


        // Print the response packet header and payload
            System.out.println("---------- Ping Response Packet Header ----------");
                System.out.println(packet_version);
                System.out.println(packet_clientID);
                System.out.println(packet_sequenceNum);
                System.out.println(packet_timestamp);
                System.out.println(packet_payloadSize);
                System.out.println("--------- Ping Response Packet Payload ------------");
                System.out.println(sendHost);
                System.out.println(sendClass);
                System.out.println(sendUser);
                System.out.println(sendRest);
            System.out.println("---------------------------------------" + "\n");

        } // end while; loop back to accept a new client connection
    } // end main
}
