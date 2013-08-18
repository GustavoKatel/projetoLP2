package br.ufpb.lp2.ccarro.testes;

//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;

public class TesteClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 5100);
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//		DataInputStream in = new DataInputStream(socket.getInputStream());
		//
		Random r = new Random(System.currentTimeMillis());
		//double lat = -7.135855, lon = -34.842932;
		float lat = rand(r, -90,90), lon = rand(r, -180,180);
		float lat_c = -7.135855f, lon_c = -34.842932f;
		float lat_step = (lat>lat_c ? lat-lat_c : lat_c-lat ) / 500;
		float lon_step = (lon>lon_c ? lon-lon_c : lon_c-lon ) / 500;
		
		while(true)
		{
			lat_c+=lat_step;
			lon_c+=lon_step;
			//
			if(lat_c>=lat || lon_c>=lon)
			{
				lat = rand(r, -90,90);
				lon = rand(r, -180,180);
				lat_step = (lat>lat_c ? lat-lat_c : lat_c-lat ) / 500;
				lon_step = (lon>lon_c ? lon-lon_c : lon_c-lon ) / 500;
			}
			//
			out.writeUTF("setLocation:"+lat_c+"#"+lon_c);
			Thread.sleep(2000);
		}
	}
	
	public static float rand(Random r, float rangeMin, float rangeMax)
	{
		return rangeMin + (rangeMax - rangeMin) * r.nextFloat();
	}

}
