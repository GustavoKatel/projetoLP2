package br.ufpb.lp2.ccarro;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import br.ufpb.lp2.ccarro.controller.DeviceListener;
import br.ufpb.lp2.ccarro.controller.DeviceManager;
import br.ufpb.lp2.ccarro.controller.MapManager;
import br.ufpb.lp2.ccarro.model.Device;

import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class Main {

	//
	private DeviceManager deviceMgr; 
	private MapManager mapMgr;
	//
	
	private Browser browser;
	private List devices_list;
	
	protected Shell shlCadeMeuCarro;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlCadeMeuCarro.open();
		shlCadeMeuCarro.layout();
		//
		shlCadeMeuCarro.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  System.out.println("Interrompendo threads...");
		    	  try{
		    		  mapMgr.close();
			    	  mapMgr.join();
			    	  deviceMgr.close();
			    	  deviceMgr.join();
		    	  }catch (InterruptedException e) {
					e.printStackTrace();
				}
		      }
		    });
		//
		initManagers();
		//
		while (!shlCadeMeuCarro.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlCadeMeuCarro = new Shell();
		shlCadeMeuCarro.setSize(666, 475);
		shlCadeMeuCarro.setText("Cade Meu Carro?");
		shlCadeMeuCarro.setLayout(new GridLayout(2, false));
		
		devices_list = new List(shlCadeMeuCarro, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		GridData device_list_gridData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		device_list_gridData.widthHint=200;
		devices_list.setLayoutData(device_list_gridData);
		devices_list.add("Todos");
		devices_list.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		    	  if(devices_list.isSelected(0))
		    	  {
		    		  mapMgr.setVisible("all", true);
		    		  return;
		    	  }
		          for(int i=1;i<devices_list.getItems().length;i++)
		        	  mapMgr.setVisible(devices_list.getItems()[i], devices_list.isSelected(i));
		        }
		});
		
		browser = new Browser(shlCadeMeuCarro, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		browser.addProgressListener(new ProgressListener() {
			
			@Override
			public void completed(ProgressEvent arg0) {
				System.out.println("Completed");
			}
			
			@Override
			public void changed(ProgressEvent arg0) {
				System.out.println(arg0.current+"/"+arg0.total);
			}
		});
	}

	private void initManagers()
	{
		deviceMgr = new DeviceManager();
		deviceMgr.addDeviceListener(listener);
		deviceMgr.start();
		mapMgr = new MapManager(deviceMgr, browser);
		mapMgr.start();
	}
	
	private DeviceListener listener = new DeviceListener() {
		
		@Override
		public void onNewDevice(final Device d) {
			Main.this.shlCadeMeuCarro.getDisplay().syncExec(new Runnable() {
				public void run() {
				    devices_list.add(d.getDName());
				}}); 
		}

		@Override
		public void onDeviceOut(final Device d) {
			Main.this.shlCadeMeuCarro.getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					devices_list.remove(d.getDName());
				}
			});
		}
	};
	
}
