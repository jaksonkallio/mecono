package mecono;

// Jakson Kallio, 2019

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class IPCHost {
	
	public IPCHost(int port) throws IOException {
		this.server = new ServerSocket(port, 10);
	}
	
	private void listen() throws Exception {
		String data = null;
		Socket client = this.server.accept();
		String clientAddress = client.getInetAddress().getHostAddress();
		System.out.println("\r\nNew connection from " + clientAddress);

		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));    
		
		while ( (data = in.readLine()) != null ) {
			System.out.println("\r\nMessage from " + clientAddress + ": " + data);
		}
    }
	
	public int getPort(){
		return server.getLocalPort();
	}
	
	private ServerSocket server;
}
