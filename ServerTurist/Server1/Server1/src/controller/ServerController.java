package controller;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ServerController {
	
	private static Connection connection;

	private Thread thread;// -means rethink-
	private ClientController client;// -means rethink-
	
	private static final int port = 7171;
	private static ServerSocket serverSocket;
	private Socket socket;
	
	public void start() {
		
		this.sqlConnection();
		
		try {
			serverSocket = new ServerSocket(port);
			while(true) {
				socket = serverSocket.accept(); // wait for client
				
				client = new ClientController();// client accepted, new client created
				
				thread = new Thread(client);// create thread for client

				client.set(socket);// set socket and thread in client
				
				thread.start();// start thread for client
			}
		} catch (Exception e) {
			System.out.println("IOException :"+e);
		}
		
		
	}
	
	private void sqlConnection() {
		try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","server01","server01");
		}catch(Exception e) {
			System.out.println("Eroare conexiune baza de date. Class: Server. Method: sqlConnection");
			e.printStackTrace();
		}
	}
	
	protected static boolean checkUser(String username, String password) {
		try {
		Statement stmt = connection.createStatement();
		
		ResultSet r = stmt.executeQuery("select * from credentials");
		while(r.next()) {
			if(username.equals(r.getString(1)) && password.equals(r.getString(2))) {
				return true;
			} 
		}
		
		}catch(Exception e) {
			System.out.println("Eroare statement. Class: Server. Method: checkUser");
		}
		return false;
	}
	
	protected static boolean insertUser(String username, String password) {
		try {
		Statement stmt = connection.createStatement();
		
		ResultSet r = stmt.executeQuery("select username from credentials");
		
		while(r.next()) {
			if(username.equals(r.getString(1))) {
				return false;
			} 
		}
		
		stmt.executeQuery("insert into credentials (username, password) values ( '"+username+"' , '"+password+"' )");
		
		return true;
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
