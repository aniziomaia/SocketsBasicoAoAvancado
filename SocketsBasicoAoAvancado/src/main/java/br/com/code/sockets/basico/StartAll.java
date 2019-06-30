package br.com.code.sockets.basico;

public class StartAll {

	public static void main(String[] args) {

		Server server = new Server();
		server.main(null);
		
		Cliente cliente = new Cliente();
		cliente.main(null);
	}

}
