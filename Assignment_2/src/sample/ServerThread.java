// Agilan Ampigaipathar (100553054)
package sample;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {

    protected Socket socket       = null;
    protected PrintWriter out     = null;
    protected BufferedReader in   = null;


    public ServerThread (Socket socket) {
        super();
        this.socket = socket;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOEXception while opening a read/write connection");
        }
    }

    public void run() {

        boolean endOfSession = false;
        while(!endOfSession) {
            endOfSession = processCommand();
        }
        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    // Cover the commands as instructed (dir, download, upload)
    protected boolean processCommand() {
        String message = null;
        try {
            message = in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading command from socket.");
            return true;
        }
        if (message.equalsIgnoreCase("DIR")) {
            System.out.println("dir");
            File folder = new File("serverFiles");
            File[] list = folder.listFiles();
            for (int i = 0; i < list.length; i++){
                out.println(list[i].getName());
            }
            return true;
        }
        else if (message.equalsIgnoreCase("DOWNLOAD")) {
            try {
                message = in.readLine();
                System.out.println(message);
                BufferedReader br = new BufferedReader(new FileReader("serverFiles/"+message));
                String line;
                //Read line by line
                while ((line = br.readLine()) != null) {
                    // process the line.
                    out.println(line);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        else if (message.equalsIgnoreCase("UPLOAD")) {
            try {
                message = in.readLine();
                System.out.println(message);
                BufferedWriter writer = null;
                writer = new BufferedWriter(new FileWriter("serverFiles/"+message));
                while((message = in.readLine()) != null) {
                    writer.append(message);
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        else
        {
            return true;
        }
    }
}

