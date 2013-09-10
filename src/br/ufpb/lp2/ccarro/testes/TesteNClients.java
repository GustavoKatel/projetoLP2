package br.ufpb.lp2.ccarro.testes;

public class TesteNClients extends Thread {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException 
	{
		int n=80;
		for(int i=0;i<n;i++)
		{
			(new TesteNClients()).start();
			Thread.sleep(1000);
		}
	}
	
}
