package br.ufpb.lp2.ccarro;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;

import br.ufpb.lp2.ccarro.controller.DeviceManager;
import br.ufpb.lp2.ccarro.controller.MapManager;

public class Main {

	//
	private DeviceManager deviceMgr; 
	private MapManager mapMgr;
	//
	
	private Browser browser;
	
	protected Shell shell;

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
		shell.open();
		shell.layout();
		//
		shell.addListener(SWT.Close, new Listener() {
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
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(666, 475);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		browser = new Browser(shell, SWT.NONE);

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
		deviceMgr.start();
		mapMgr = new MapManager(deviceMgr, browser);
		mapMgr.start();
	}
	
}
