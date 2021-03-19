import javax.swing.JFrame;

public class ServerTest {
	public static void main(String[] args) {
		Server serv1 = new Server();
		serv1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		serv1.startRunning();
	}
}
