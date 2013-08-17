package br.ufpb.lp2.ccarro.testes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TesteClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 5100);
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		DataInputStream in = new DataInputStream(socket.getInputStream());
		//
		double lat = -7.135855, lon = -34.842932;
		while(true)
		{
			lat+=1;
			out.writeUTF("setLocation:"+lat+"#"+lon);
			Thread.sleep(2000);
		}
	}

}
