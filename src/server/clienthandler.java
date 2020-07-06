
package server;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.Instant;
public class clienthandler implements Runnable  {
   private Socket conn = null;
   public PrintStream print = null;  
   public DataInputStream input = null;
   public Connection mysqlConn;
   public Statement myStmt = null;
   public ResultSet result = null;
   public DataOutputStream output = null;
   public adminPriv admpriv =new adminPriv(conn);
   clienthandler (Socket c) {      
      this.conn = c;
      
   }

   public void run()  {
      String line = "";
       
      try {
        input = new DataInputStream(conn.getInputStream());
        System.out.println(Instant.now());
	System.out.println("selamat datang");
        line = input.readUTF();
        String split[] = line.split(","); 
        System.out.println(line);
        
                switch(split[0]){
                    case "cari":
                        try{ 
                            if (!"null".equals(split[1])){
                                search(split[1]);
                                
                            }
                            else{
                                output = new DataOutputStream(conn.getOutputStream());
                                output.writeUTF("cari");
                                line = input.readUTF();
                                search(line);
                            }
                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }                        
                        break;
                        
                    case "login":
                        
                        try{
                            output = new DataOutputStream(conn.getOutputStream());
                            output.writeUTF("login");
                            login();
                            //skipped for some reason. Fix later
                            admpriv.run();
                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                        break;                             
                }
            
        conn.close();
        System.out.println("selamat jalan");
        }
        
      
      catch (Exception e) {
          System.out.println(e.getMessage());
      }   
   }
   
  
   
   public void search (String s) throws IOException,SQLException{
           
           mysqlConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpus_app","root","");
           print = new PrintStream(conn.getOutputStream());
           
           while(!"bye".equals(s)) 
           {
               myStmt = mysqlConn.createStatement();
               result = myStmt.executeQuery("SELECT * FROM daftar_buku WHERE judul like '%" + s +"%' OR pengarang like '%"+ s +"%'");
               System.out.println(s);
               while (result.next())
                {
                    int id = result.getInt("id");                             
                    String judul = result.getString("judul");                    
                    String penulis =result.getString("pengarang");                                      
                    String status = result.getString("status_buku");
                    int tahun =result.getInt("tahun_terbit");
                    print.format("%s, %s, %s, %s\n", judul, penulis, tahun, status);
                    System.out.format("%s, %s, %s, %s, %s\n", id, judul, penulis, tahun, status);
                    
                }
                s = input.readUTF();
           }
           print.println("done");
           
           conn.close();
   }
   public  String login() throws IOException, SQLException{
        String user = input.readUTF();
        System.out.println(user);
        String pwd =input.readUTF();
        print = new PrintStream(conn.getOutputStream());
        String hasil = "";
        mysqlConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpus_app","root","");
        myStmt = mysqlConn.createStatement();
        String sql = "SELECT * FROM admin WHERE user='"+user+"' AND password='"+pwd+"'";
        result = myStmt.executeQuery(sql);
            if(result.next()){
                if(user.equals(result.getString("user")) && pwd.equals(result.getString("password"))){
                    hasil = "sukses";
                    print.println(hasil);
                    
                }
                //admpriv.run();
            }                        
            else{
                hasil = "gagal";                
                print.println(hasil);                    
                login();
            }
            return hasil;
       }
   }


