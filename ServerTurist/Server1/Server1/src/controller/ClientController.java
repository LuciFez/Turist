package controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientController extends ServerController implements Runnable {

	private Socket socket;

	private InputStream input;
	private OutputStream output;

	private BufferedReader reader;
	private PrintWriter writer;

	private String message;

	public void set(Socket s) {
		socket = s;
	}

	@Override
	public void run() {
		try {
			input = socket.getInputStream();
			output = socket.getOutputStream();

			reader = new BufferedReader(new InputStreamReader(input));
			writer = new PrintWriter(output, true);

			while (true) {
				message = reader.readLine();
				if(message == null) {
					break;
				}
				System.out.println(message);
				if (message.substring(0, 5).equals("login")) {

					message = message.substring(message.indexOf('{') + 1, message.indexOf('}'));
					
					String user = message.substring(0, message.indexOf(':'));
					String pass = message.substring(message.indexOf(':') + 1);
					
					if(user.isEmpty() || pass.isEmpty()) {
						writer.println("wrong");
					}else {
						if (super.checkUser(user,pass) == true) {
							writer.println("connected");
							break;
						} else {
							writer.println("wrong");
						}
					}

				} else if (message.substring(0, 8).equals("register")) {
					message = message.substring(message.indexOf('{') + 1, message.indexOf('}'));

					if (super.insertUser(message.substring(0, message.indexOf(':')),message.substring(message.indexOf(':') + 1)) == false) {
						writer.println("wrong");
					} else {
						writer.println("inserted");
						break;
					}

				}
			}

		} catch (Exception e) {
			System.out.println("Erorr Client Controller\n");
			e.printStackTrace();
		}

	}

}
