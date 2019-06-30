/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.code.sockets.avancado;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author elder
 */
public class Server {
    /* 
     1 - Criar o servidor de conexÃµes
     2 -Esperar o um pedido de conexÃ£o;
     Outro processo
     2.1 e criar uma nova conexÃ£o;
     3 - Criar streams de enechar socket de comunicaÃ§Ã£o entre servidor/cliente
     4.2 - Fechar streams de entrada e saÃ­da
     trada e saÃ­da;
     4 - Tratar a conversaÃ§Ã£o entre cliente e 
     servidor (tratar protocolo);
     4.1 - Fechar socket de comunicaÃ§Ã£o entre servidor/cliente
     4.2 - Fechar streams de entrada e saÃ­da
           
     5 - voltar para o passo 2, atÃ© que finalize o programa;
     6 - fechar serverSocket
     */

    private ServerSocket serverSocket;
    private int contConexoesAbertas;
    /*- Criar o servidor de conexÃµes*/

    private void criarServerSocket(int porta) throws IOException {
    	System.out.println("........Server.criarServerSocket");
        serverSocket = new ServerSocket(porta);
        contConexoesAbertas = 0;
    }
    

    /*2 -Esperar o um pedido de conexÃ£o;
     Outro processo*/
    private Socket esperaConexao() throws IOException {
    	System.out.println("........Server.esperaConexao");
        Socket socket = serverSocket.accept();
        return socket;
    }

    private void fechaSocket(Socket s) throws IOException {
    	System.out.println("........Server.fechaSocket");
    	System.out.println("QTD Conexões que foram abertas: " + contConexoesAbertas);
    	contConexoesAbertas = 0;
        s.close();
    }

    private void enviaMsg(Object o, ObjectOutputStream out) throws IOException {
    	System.out.println("........Server.enviaMsg");
        out.writeObject(o);
        out.flush();
    }

    private void trataConexao(Socket socket) throws IOException, ClassNotFoundException {
        // * Cliente ------SOCKET-----servidor
        //protocolo da aplicaÃ§Ã£o
        /*
         4 - Tratar a conversaÃ§Ã£o entre cliente e 
         servidor (tratar protocolo);
         */

        try {
            /* 3 - Criar streams de entrada e saÃ­da;*/

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            /*protocolo
             HELLO
             nome : String
             sobrenome: String
            
             HELLOREPLY
             OK, ERRO, PARAMERROR
             mensagem : String
            
             */
            /*4 - Tratar a conversaÃ§Ã£o entre cliente e 
             servidor (tratar protocolo);*/
            System.out.println("Tratando...");
            Estados estado = Estados.CONECTADO;
            while (estado != Estados.SAIR) {

                Mensagem m = (Mensagem) input.readObject();
                System.out.println("Mensagem do cliente:\n" + m);
              

                String operacao = m.getOperacao();
                Mensagem reply = new Mensagem(operacao + "REPLY");
                //estados conectado autenticado
                switch (estado) {
                    case CONECTADO:
                        switch (operacao) {
                            case "LOGIN":
                                try {
                                    String user = (String) m.getParam("user");
                                    String pass = (String) m.getParam("pass");

                                    if (user.equals("ALUNO") && pass.equals("ESTUDIOSO")) {
                                        reply.setStatus(Status.OK);
                                        estado = Estados.AUTENTICADO;
                                    } else {
                                        reply.setStatus(Status.ERROR);
                                    }

                                } catch (Exception e) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("msg", "Erro nos parÃ¢metros do protocolo.");
                                }
                                break;
                            case "HELLO":
                                String nome = (String) m.getParam("nome");
                                String sobrenome = (String) m.getParam("sobrenome");

                                reply = new Mensagem("HELLOREPLY");

                                if (nome == null || sobrenome == null) {
                                    reply.setStatus(Status.PARAMERROR);
                                } else {
                                    reply.setStatus(Status.OK);
                                    reply.setParam("mensagem", "Hello World, " + nome + " " + sobrenome);

                                }
                                break;
                            case "SAIR":
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;
                            default:
                                //responder mensagem de erro: NÃ£o autorizado/ou invÃ¡lida
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NÃƒO AUTORIZADA OU INVÃ�LIDA!");

                                break;
                        }
                        break;
                    case AUTENTICADO:
                        switch (operacao) {
                            case "DIV":
                                try {

                                    Integer op1 = (Integer) m.getParam("op1");
                                    Integer op2 = (Integer) m.getParam("op2");
                                    //testar os dados
                                    reply = new Mensagem("DIVREPLY");
                                    if (op2 == 0) {
                                        reply.setStatus(Status.DIVZERO);
                                    } else {
                                        reply.setStatus(Status.OK);
                                        System.out.println("Op1: " + op1 + " Op2: " + op2);
                                        float div = (float) op1 / op2;
                                        reply.setParam("res", div);
                                    }
                                } catch (Exception e) {
                                    reply = new Mensagem("DIVREPLY");
                                    reply.setStatus(Status.PARAMERROR);
                                }
                                break;
                            case "SUB":
                                break;
                            case "MUL":
                                break;
                            case "SOMA":
                                break;
                            case "LOGOUT":
                                reply.setStatus(Status.OK);
                                estado = Estados.CONECTADO;
                                break;
                            case "SAIR":
                                //DESIGN PATTERN STATE
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;
                            default:
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NÃƒO AUTORIZADA OU INVÃ�LIDA!");
                                break;
                        }
                        break;
                    case SAIR: //ESTADP
                        break;

                }

                output.writeObject(reply);
                output.flush();//cambio do rÃ¡dio amador
            }
            //4.2 - Fechar streams de entrada e saÃ­da
            input.close();
            output.close();
        } catch (IOException e) {
            //tratamento de falhas
            System.out.println("Problema no tratamento da conexÃ£o com o cliente: " + socket.getInetAddress());
            System.out.println("ErroServer: " + e.getMessage());
            throw e;
        } finally {
            //final do tratamento do protocolo
            /*4.1 - Fechar socket de comunicaÃ§Ã£o entre servidor/cliente*/
            fechaSocket(socket);
        }

    }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException {
        try {

            Server server = new Server();

            server.criarServerSocket(5555);
            while (true) {
                System.out.println("Aguardando conexÃ£o...");
                Socket socket = server.esperaConexao();//protocolo
                System.out.println("Cliente conectado.");
                //Outro processo
                
                server.trataConexao(socket);
                System.out.println("Cliente finalizado.");
            }
        } catch (IOException e) {
            //trata exceÃ§Ã£o
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

}
