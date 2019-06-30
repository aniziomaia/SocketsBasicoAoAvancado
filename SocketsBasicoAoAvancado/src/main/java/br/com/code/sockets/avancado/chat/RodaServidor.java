package br.com.code.sockets.avancado.chat;

import java.io.IOException;

public class RodaServidor {

	public static void main(String[] args) 
			throws IOException {
		new Servidor(12345).executa();
	}
}