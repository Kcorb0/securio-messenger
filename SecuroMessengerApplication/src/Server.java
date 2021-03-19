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

public class Server extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	//constructor
	public Server() {
		super("Securio Server");
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
		
		//chat window settings
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(350,500);
		setVisible(true);
	}
	
	//set up and run the server
	public void startRunning() {
		try {
			server = new ServerSocket(6789, 30);
			while(true) {
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException) {
					showMessage("\n Server ended the connection! ");
				}finally {
					closeConnections();
				}
				
			}
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//wait for connection, then display connection information
	private void waitForConnection() throws IOException{
		showMessage(" Waiting for someone to connect... \n");
		connection = server.accept();
		showMessage(" Now connected to " + connection.getInetAddress().getHostName());
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup! \n");
	}
	
	//during the chat conversation
	private void whileChatting() throws IOException{
		showMessage(" SERVER - You are now connected!");
		String message = "Hello";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n Invalid string sent! ");
			}
		}while(!message.equals(" CLIENT - END"));
	}
	
	//close streams and sockets when conversation ends
	private void closeConnections() {
		showMessage("\n Closing connections... \n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	
	
	
	//send a message to client
	private void sendMessage(String message) {
		
		//Set String byte key and byte data as the input for encryption
		byte[] key = "754634750829456872451034".getBytes();
		byte[] data = message.getBytes();
		
		//Encryption type for key set to null
		Key deskey = null;
		
		//Set 3DES key using the previously set key
		DESedeKeySpec spec;
		try {
			spec = new DESedeKeySpec(key);
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
			//key for encrypt
			deskey = keyfactory.generateSecret(spec);
			
			//desede (encrypt decrypt encrypt)
			Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, deskey);
			
			byte[] CipherText = cipher.doFinal(data);
			
			//Convert byte cipher text to hex
			StringBuffer hexCiphertext = new StringBuffer();
			for (int i=0;i<CipherText.length;i++)
				hexCiphertext.append(Integer.toString((CipherText[i]&0xff)+0x100,16).substring(1));
			
			output.writeObject("SERVER - " + hexCiphertext);
			output.flush();
			showMessage("\n SERVER Encrypted - " + hexCiphertext);
			
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			byte[] plaintext = cipher.doFinal(CipherText);
			
			showMessage("\n SERVER Decrypted - " + new String(plaintext));
			
		} catch(IOException ioException) {
			chatWindow.append("\n ERROR: INVALID MESSAGE ");
		} catch (InvalidKeyException ex) {
			chatWindow.append("\n ERROR: INVALID KEY ");
		} catch (NoSuchAlgorithmException ex) {
			chatWindow.append("\n ERROR: INVALID ALGORITHM ");
		} catch (InvalidKeySpecException ex) {
			chatWindow.append("\n ERROR: INVALID KEYSPEC ");
		} catch (NoSuchPaddingException ex) {
			chatWindow.append("\n ERROR: NO SUCH PADDING ");
		} catch (IllegalBlockSizeException ex) {
			chatWindow.append("\n ERROR: ILLEGAL BLOCK SIZE ");
		} catch (BadPaddingException ex) {
			chatWindow.append("\n ERROR: BAD PADDING ");
		}
	}
	
	
	
	
	
	//update chat window
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(text);
				}
			}
		);
	}
	
	//allow user to input message to message box
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
