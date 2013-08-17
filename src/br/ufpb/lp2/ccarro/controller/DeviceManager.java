package br.ufpb.lp2.ccarro.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.ufpb.lp2.ccarro.model.Device;
import br.ufpb.lp2.ccarro.model.Location;

public class DeviceManager extends Thread {

	private LinkedList<Device> devices;
	private ExecutorService pool;
	private HashSet<DeviceListener> listeners;
	
	private ServerSocket server;

	private boolean running = true;
	
	public DeviceManager()
	{
		devices = new LinkedList<Device>();
		listeners = new HashSet<DeviceListener>();
		//
		pool = Executors.newCachedThreadPool();
		//
		try {
			server = new ServerSocket(5100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while(running)
		{
			try {
				System.out.println("Aguardando dispositivos");
				Socket s = server.accept();
				System.out.println("Novo dispositivo em "+s.getInetAddress()+":"+s.getPort());
				final Device d = new Device("device_"+String.valueOf(devices.size()+1), s);
				devices.add(d);
				pool.submit(d);
				for(DeviceListener l : listeners)
					l.onNewDevice(d);
			} catch (IOException e) {
				break;
//				e.printStackTrace();
			}
		}
		System.out.println("DeviceManager fechado!");
	}
	
	public synchronized void close()
	{
		try {
			//
			for(Device d : devices)
				d.close();
			//
			pool.shutdown();
			pool.shutdownNow();
			//
			running=false;
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LinkedList<Device> getDevices()
	{
		return devices;
	}
	
	public void addDeviceListener(DeviceListener l)
	{
		listeners.add(l);
	}
	
}