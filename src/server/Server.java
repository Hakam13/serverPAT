
package server;

import java.net.*;
public class Server {


    public static void main(String[] args) {
        ServerSocket s = null;
        Socket conn = null;
        
        try{
            s = new ServerSocket(9999,20);
            System.out.println("menunggu sambungan");
            while (true){
                conn = s.accept();
                System.out.println("sambungan dari "+conn.getInetAddress().getHostName());
                clienthandler tHandler = new clienthandler(conn);
                Thread thread = new Thread(tHandler);
                thread.start();
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
