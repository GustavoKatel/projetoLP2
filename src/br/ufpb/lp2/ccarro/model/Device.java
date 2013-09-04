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

	/* 0 - normal, 1 - toDelete, 2 - removed */
	private int state = 0;
	
	private boolean running = true;
	
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
			state=1;
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		//receive location from socket
		while(running)
		{
			try {
				//
				String cmd = instream.readUTF();
//				System.out.println("cmd recebido: "+cmd+" de "+this.socket.getInetAddress()+":"+this.socket.getPort());
				if(cmd.startsWith("setLocation:"))
				{
					if(cmd.indexOf('#')<0)
					{
						System.out.println("Erro de protocolo. Mensagem: "+cmd);
					} else {
						String lat, lon;
						lat = cmd.split("#")[0];
						lat = lat.replaceAll("setLocation:", "");
						lon = cmd.split("#")[1];
						//
//						System.out.println("lat: "+lat+" lon: "+lon);
						//
						this.location.setLat(Double.parseDouble(lat));
						this.location.setLon(Double.parseDouble(lon));
					}
				}
			}catch (IOException e) {
				close();
//				e.printStackTrace();
			}
		}
		System.out.println("Device ("+this.dName+") fechado.");
	}

	public synchronized void close()
	{
		try {
			state=1;
			this.running = false;
			this.instream.close();
			this.outstream.close();
			this.socket.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	public synchronized void setRemovedFromJS()
	{
		this.state=2;
	}
	
	/**
	 * 0 - normal
	 * 1 - toDelete
	 * 2 - deletedFromJS
	 * @return the state
	 */
	public synchronized int getState()
	{
		return state;
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
