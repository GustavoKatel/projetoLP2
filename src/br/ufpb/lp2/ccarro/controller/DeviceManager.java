package br.ufpb.lp2.ccarro.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.ufpb.lp2.ccarro.model.Device;

public class DeviceManager extends Thread {

	private LinkedList<Device> devices;
	private ExecutorService pool;
	private HashSet<DeviceListener> listeners;
	
	private ServerSocket server;

	private Thread device_monitor;
	
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
		//
		device_monitor = new Thread(device_monitor_run);
		device_monitor.start();
	}
	
	public void run()
	{
		while(running)
		{
			try {
				System.out.println("Aguardando dispositivos");
				Socket s = server.accept();
				System.out.println("Novo dispositivo em "+s.getInetAddress()+":"+s.getPort());
				synchronized (this) {
					Device d = new Device("device_"+s.getInetAddress().getHostAddress(), s);
					devices.add(d);
					pool.submit(d);
					for(DeviceListener l : listeners)
						l.onNewDevice(d);
				}
			} catch (IOException e) {
				break;
//				e.printStackTrace();
			}
		}
		System.out.println("DeviceManager fechado!");
	}
	
	public synchronized void remove(Device d)
	{
		this.devices.remove(d);
		for(DeviceListener l : listeners)
			l.onDeviceOut(d);
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
			//
			device_monitor.interrupt();
			device_monitor.join();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
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
	
	private Runnable device_monitor_run = new Runnable() {
		
		@Override
		public void run() {
			Device d = null;
			while(running)
			{
				for(int i=0;i<devices.size();i++)
				{
					d = devices.get(i);
					if(d.getState()==2)
					{
						remove(d);
						i=0;
					}
					if(!running) break;
				}
			}
		}
	};
	
}