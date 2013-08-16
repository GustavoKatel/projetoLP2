package br.ufpb.lp2.ccarro.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Device implements Runnable {

	private String dName;
	private Location location;
	private Socket socket;
	
	private DataInputStream instream;
	private DataOutputStream outstream;
	
	public Device(String dName, Socket s)
	{
		this.dName = dName;
		this.socket = s;
		this.location = new Location();
		//
		try {
			instream = new DataInputStream(socket.getInputStream());
			outstream = new DataOutputStream(socket.getOutputStream());
			//
			outstream.writeUTF("setName:"+this.dName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		//receive location from socket
		while(true)
		{
			try {
				//
				String cmd = instream.readUTF();
				if(cmd.startsWith("setLocation:"))
				{
					if(cmd.indexOf('|')<0)
					{
						System.out.println("Erro de protocolo. Mensagem: "+cmd);
					} else {
						String lat, lon;
						lat = cmd.split("|")[0];
						lat.replaceAll("setLocation:", "");
						lon = cmd.split("|")[1];
					}
				}
				//
				Thread.sleep(2000);
			}catch (Exception e) {
				break;
//				e.printStackTrace();
			}
		}
	}

	public String getDName() {
		return dName;
	}

	public void setDName(String dName) {
		this.dName = dName;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
}