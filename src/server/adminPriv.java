
package server;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class adminPriv {
    private Socket socket = null;
    public Connection mysqlConn;
    public DataInputStream input = null;
    adminPriv(Socket c){
        this.socket = c;
        
    }
    public void run(){
        String line = "";
        
        try{
            
            input = new DataInputStream(socket.getInputStream());
            line = input.readUTF();
            //loop doesn't work. Need to find a way to make it loop
            while(true){
                switch(line){
                    case "circulation":
                        try{
                            circulation();
                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                        break;
                        case "bookDb":
                            try{
                                bookDbUpdate();
                            }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "userDb":
                        try{
                            userDbUpdate();
                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                        break;
                }
                line=input.readUTF();
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void bookDbUpdate() throws SQLException, IOException{
        mysqlConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpus_app","root","");
        input = new DataInputStream(socket.getInputStream());
        String title =input.readUTF();
        String isbn =input.readUTF();
        String year =input.readUTF();
        String publisher =input.readUTF();
        String author =input.readUTF();
        String status =input.readUTF();
        int ISBN = Integer.parseInt(isbn);
        int pubYear = Integer.parseInt(year);
        String query = "insert into daftar_buku(judul, ISBN, tahun_terbit, penerbit, pengarang, status_buku)"+"values(?,?,?,?,?,?)";
        PreparedStatement preparedStmt = mysqlConn.prepareStatement(query);
        preparedStmt.setString(1, title);
        preparedStmt.setInt(2, ISBN);
        preparedStmt.setInt(3, pubYear);
        preparedStmt.setString(4, publisher);
        preparedStmt.setString(5, author);
        preparedStmt.setString(6, status);
    }
    public void userDbUpdate() throws SQLException, IOException{
        mysqlConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpus_app","root","");
        input = new DataInputStream(socket.getInputStream());
        String uname = input.readUTF();
        String password = input.readUTF();
        String query ="insert into admin(user, password)"+"values(?,?)";
        PreparedStatement preparedStmt = mysqlConn.prepareStatement(query);
        preparedStmt.setString(1, uname);
        preparedStmt.setString(2, password);
    }
    public void circulation() throws SQLException,IOException{
        mysqlConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpus_app","root","");
        input = new DataInputStream(socket.getInputStream());
        String title =input.readUTF();
        String year = input.readUTF();
        String status =input.readUTF();
        String query="update daftar_buku set status_buku = ? where judul=? && tahun_terbit=?";
        PreparedStatement preparedStmt = mysqlConn.prepareStatement(query);
        preparedStmt.setString(1, status);
        preparedStmt.setString(2, title);
        preparedStmt.setString(3, year);
    }
}
