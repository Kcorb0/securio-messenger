import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.security.Key;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	//constructor
	public Client(String host) {
		super("Securio Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(350, 500);
		setVisible(true);
	}
	
	//connect to server
	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofException){
			showMessage("\n Client terminated connection");
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}finally {
			closeConnections();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		showMessage(" Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage(" Connected to:" + connection.getInetAddress().getHostName());
	}
	
	//set up streams to send and recieve messages
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Stream is active! \n");
	}
	
	//while chatting with server
	private void whileChatting() throws IOException{
		ableToType(true);
		showMessage(" SERVER - You are now connected!");
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n " + message);
				
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n Unknown object type");
			}
		}while(!message.equals(" SERVER - END"));
	}
	
	//close the streams and sockets
	private void closeConnections() {
		showMessage("\n closing connections...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//send messages to server
	private void sendMessage (String message) {
		try {
			output.writeObject(" CLIENT - " + message);
			output.flush();
			showMessage("\n CLIENT - " + message);
		}catch(IOException ioException) {
			chatWindow.append("\n something went wrong sending message!");
		}
	}
	
	//change / update chat window
	private void showMessage(final String m) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(m);
				}
			}
		);
	}
	
	//gives user permission to type within text box
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					userText.setEditable(tof);
				}
			}
		);
	}
}
