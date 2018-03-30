// Agilan Ampigaipathar (100553054)
package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket socketServer;
    private Socket acceptSocket;
    private PrintStream output;
    private BufferedReader input;
    protected ServerThread[] threads    = null;

    public static int MAX_CLIENTS = 25;
    protected int numClients = 0;

    public static void main (String[] args) {
        Server server = new Server();
        server.serverRun();
    }

    public void serverRun() {
        try {
            socketServer = new ServerSocket(9999);
            threads = new ServerThread[MAX_CLIENTS];

            // While there is not the max client runs within a single server run
            while(numClients != 25) {
                acceptSocket = socketServer.accept();

                output = new PrintStream(acceptSocket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(acceptSocket.getInputStream()));

                threads[numClients] = new ServerThread(acceptSocket);
                threads[numClients].start();
                numClients++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
