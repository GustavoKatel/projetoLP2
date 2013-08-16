package br.ufpb.lp2.ccarro.controller;

import java.io.File;
import java.util.LinkedList;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Display;

import br.ufpb.lp2.ccarro.model.Device;

public class MapManager extends Thread {

	private DeviceManager mgr;
	private Browser brw;
	
	private boolean running = true;
	
	private boolean jsOk = false;
	private Object jsOk_lock = new Object();
	
	private LinkedList<Runnable> enqueuedMarkers;
	
	public MapManager(DeviceManager mgr, Browser brw)
	{
		this.mgr = mgr;
		this.brw = brw;
		//
		initialize();
		//
		enqueuedMarkers = new LinkedList<Runnable>();
	}
	
	public void run()
	{
		while(running)
		{
			synchronized (jsOk_lock) {
				if(!jsOk) continue;
			}
			for(final Device d : mgr.getDevices())
			{
				execute(new Runnable() {
					
					@Override
					public void run() {
						brw.evaluate("updateMarker("+
								d.getLocation().getLat()+","+
								d.getLocation().getLon()+
								",\""+d.getDName()+"\");");
					}
				});
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				break;
			}
		}
		System.out.println("Fechando MapManager");
	}
	
	private void initialize()
	{
		File f = new File("etc/map.html");
		brw.setUrl(f.toURI().toString());
		//
		brw.addProgressListener(new ProgressListener() {
			
			@Override
			public void completed(ProgressEvent arg0) {
				for(Runnable r : enqueuedMarkers)
					execute(r);
				enqueuedMarkers.clear();
				synchronized(jsOk_lock)
				{
					jsOk = true;
				}
			}
			
			@Override
			public void changed(ProgressEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
		//
		mgr.addDeviceListener(new DeviceListener() {
			
			@Override
			public void onNewDevice(final Device d) {
				Runnable r = new Runnable() {
					
					@Override
					public void run() {
						brw.evaluate("updateMarker("+
								d.getLocation().getLat()+","+
								d.getLocation().getLon()+
								",\""+d.getDName()+"\");");
					}
				};
				synchronized (jsOk_lock) {
					if(jsOk)
						execute(r);
					else
						enqueuedMarkers.add(r);
				}
			}
		});
	}
	
	private void execute(Runnable r)
	{
		Display.getDefault().asyncExec(r);
	}
	
	public synchronized void close()
	{
		this.running=false;
	}
	
}
