/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.code.sockets.avancado;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elder
 */
public class Cliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            /*
             1. Estabelecer conexão com o servidor
             2. Trocar mensagens com o servidor
             */
            //cria a conexão entre o cliente e o servvidor
            System.out.println("****Cliente: Estabelecendo conexão...");
            Socket socket = new Socket("localhost", 5555);
            System.out.println("****Cliente: Conexão estabelecida.");

            //criação dos streams de entrada e saída
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            System.out.println("****Cliente: Enviando mensagem...");
            /*protocolo
             HELLO
             nome : String
             sobrenome: String
            
             HELLOREPLY
             OK, ERRO, PARAMERROR
             mensagem : String
            
             */

            Mensagem m = new Mensagem("LOGIN");
            m.setParam("user", "ALUNO");
            m.setParam("pass", "ESTUDIOSO");

            output.writeObject(m);
            output.flush();

            m = (Mensagem) input.readObject();
            System.out.println("****Cliente: Resposta: " + m);
            boolean sair = false;
            int cont = 0;
            while (!sair) {
            	cont += 1;
                m = new Mensagem("DIV");

                m.setParam("op1", 2);
                m.setParam("op2", 0);

                output.writeObject(m);
                output.flush(); //libera buffer para envio

                System.out.println("****Cliente: Mensagem " + m + " enviada.");

                m = (Mensagem) input.readObject();
                System.out.println("****Cliente: Resposta: " + m);

//                m = new Mensagem("SAIR");
//                output.writeObject(m);
//                output.flush();
//
//                m = (Mensagem) input.readObject();
//                System.out.println("Resposta: " + m);
                if(cont == 5)sair = true;
            }

            input.close();
            output.close();
            socket.close();

        } catch (IOException ex) {
            System.out.println("****Cliente: Erro no cliente: " + ex);
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("****Cliente: Erro no cast: " + ex.getMessage());
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
