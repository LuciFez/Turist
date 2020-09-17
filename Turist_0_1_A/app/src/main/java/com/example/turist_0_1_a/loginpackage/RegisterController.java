package com.example.turist_0_1_a.loginpackage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class RegisterController extends Thread {

    private static final String TAG = "RegisterController";

    private static int port = 7171;
    private static String host = "10.0.2.2";
    private static Socket socket;

    private OutputStream output;
    private PrintWriter writer;

    private InputStream input;
    private BufferedReader reader;


    private String u;
    private String p;

    public static Boolean status;

    public void set(String user, String pass) {
        u = user;
        p = pass;
        status = false;
    }

    public void run() {
        try {
            socket = new Socket(host, port);

            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));

            String recv;

            try {
                writer.println("register{"+u + ":" + p+"}");
                recv = reader.readLine();
                if (recv.equals("inserted")) {
                    status = true;
                }else{
                    //try again -- blink red or something --admin
                }

            } catch (Exception e) {
                System.out.println("Eroare trimitere/citire socket");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Eroare la creare socket");
        }

    }

}
