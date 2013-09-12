package br.ufpb.lp2.wau.testes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class TesteClient extends Thread {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException 
	{
		(new TesteClient()).start();
		Thread.sleep(1000);
	}
	
	public static float rand(Random r, float rangeMin, float rangeMax)
	{
		return rangeMin + (rangeMax - rangeMin) * r.nextFloat();
	}

	public void run()
	{
		try {
			Socket socket = new Socket("localhost", 5100);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String cmd = in.readUTF();
			String name = cmd.split(":")[1];
			System.out.println(cmd);
			//
			Random r = new Random(System.currentTimeMillis());
			//double lat = -7.135855, lon = -34.842932;
			float lat = rand(r, -90,90), lon = rand(r, -180,180);
			float lat_c = -7.135855f, lon_c = -34.842932f;
			float lat_step = (lat-lat_c) / 1000000f;
			float lon_step = (lon-lon_c) / 1000000f;
			
			System.out.println(name+": lat_step: "+lat_step+" lon_step: "+lon_step);
			
			while(true)
			{
				lat_c+=( r.nextInt(2)==0 ? lat_step : 0 );
				lon_c+=( r.nextInt(2)==0 ? lon_step : 0 );
				System.out.println(name+": lat: "+lat_c+"/"+lat+" lon_c: "+lon_c+"/"+lon);
				//
				if(Math.abs(lat_c)>=Math.abs(lat) || Math.abs(lon_c)>=Math.abs(lon))
				{
					lat = rand(r, -90,90);
					lon = rand(r, -180,180);
					lat_step = (lat-lat_c) / 1000000f;
					lon_step = (lon-lon_c) / 1000000f;
					System.out.println(name+": lat_step: "+lat_step+" lon_step: "+lon_step);
				}
				//
				out.writeUTF("setLocation:"+lat_c+"#"+lon_c);
				Thread.sleep(2000);
			}
		}catch (IOException e) {
			System.out.println("Conexão fechada");
		}catch (InterruptedException e) {
			// TODO: handle exception
		}
	}
	
}
