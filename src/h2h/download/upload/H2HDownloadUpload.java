/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2h.download.upload;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.stream.Collectors;
import java.io.File; 
import java.util.Scanner;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.time.LocalDateTime;  
import java.time.format.DateTimeFormatter;  
import java.util.Properties;

/**
 *
 * @author Admin
 */
public class H2HDownloadUpload {

    private static java.sql.Connection koneksi;
        
        private static String id,jurl,jdb,juser,jpassword,jinput,joutput;
        private static boolean loop = true;
        private static String sshHost, sshUser, sshPassword, dbHost, dbName, dbUser, dbPassword;
        private static int sshPort, dbPort;
        
        public static java.sql.Connection getKoneksi() {
        java.util.Date date = new java.util.Date(); 
        if (koneksi == null){
            try {
                JSch jsch = new JSch();
                Session session = jsch.getSession(sshUser, sshHost, sshPort);
                session.setPassword(sshPassword);

                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();

                session.setPortForwardingL(9312, dbHost, dbPort);

                String url = "jdbc:mysql://localhost:9312/" + dbName;
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                koneksi = DriverManager.getConnection(url, dbUser, dbPassword);
                System.out.println("Berhasil Terhubung "+date);
            } catch (Exception e) {
                System.out.println("Error "+e);
            }
        }
        return koneksi;
        }
        public static void loadSshSettingsFromJson(String jsonFilePath) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(jsonFilePath));
            JSONObject jsonObject = (JSONObject) obj;

            sshHost = (String) jsonObject.get("sshHost");
            sshUser = (String) jsonObject.get("sshUser");
            sshPassword = (String) jsonObject.get("sshPassword");
            sshPort = ((Long) jsonObject.get("sshPort")).intValue();

            dbHost = (String) jsonObject.get("dbHost");
            dbName = (String) jsonObject.get("dbName");
            dbUser = (String) jsonObject.get("dbUser");
            dbPassword = (String) jsonObject.get("dbPassword");
            dbPort = ((Long) jsonObject.get("dbPort")).intValue();
        } catch (Exception e) {
            System.out.println("Error reading SSH settings from JSON: "+e);
        }
    }

        public static Connection getdatanow(){
            java.util.Date date = new java.util.Date(); 
            //mengambil data di db dan mengubah ke txt lalu update data yang sudah di download
            String text = null ;
            try {
                Connection c = H2HDownloadUpload.getKoneksi();
                Statement s = c.createStatement();
//                String sql = "SELECT * FROM transferins";
                String sql = "SELECT * FROM transferins WHERE status = 0 ORDER BY id ASC LIMIT 1";
                ResultSet r = s.executeQuery(sql);
                System.out.println("Data Selected Transferin "+date);
//               System.out.println(r.getArray(sql));
                if (sql==null) {
                    System.out.println("Data Kosong");
                } else {
                        while(r.next()){
                        Object[] o = new Object[11];
                        o [0] = r.getString("id");
                        o [1] = r.getString("accountNumber");
                        o [2] = r.getString("amount");
                        o [3] = r.getString("dateTime");
                        o [4] = r.getString("referenceNumber");
                        o [5] = r.getString("terminalId");
                        o [6] = r.getString("hashCode");
                        o [7] = r.getString("created_at");
                        o [8] = r.getString("updated_at");
                        o [9] = r.getString("jenis");
                        o [10] = r.getString("status");
                        


                        text = o[9]+"|"+o[1]+"|"+o[2]+"|"+o[3]+"|"+o[4];
                        id = (String) o[0];
                        try {
//                            FileWriter myWriter = new FileWriter("D:\\testtab\\java\\txt\\input\\"+o[0]+".txt");                            
                              FileWriter myWriter = new FileWriter(jinput+""+o[0]+".txt");

                            myWriter.write(text);
                            myWriter.close();
                            System.out.println("Successfully wrote "+o[0]+".txt "+date);
                            try {

                                String sqlu = "UPDATE `transferins` SET `status` = '1' WHERE `transferins`.`id` = '"+o[0]+"'";
                                PreparedStatement u = c.prepareStatement(sqlu);

                                u.executeUpdate();
                                u.close();
                                addJava_request_data(id);
                            } catch (SQLException e) {
                                System.out.println("Eror "+e);
                            }
                        } catch (IOException e) {
                            System.out.println("An error occurred. "+e);
//                            e.printStackTrace();
                        }
                    }
                }
                r.close();
                s.close();
            } catch (SQLException e) {
                System.out.println("Eror "+e);
            }
            return koneksi;
        }
        
        public static Connection tambahHasil(){
            //mengambil data di db dan mengubah ke txt lalu update data yang sudah di download
            java.util.Date date = new java.util.Date(); 
            String jsontext =null;
            try {
                Connection tambahHasilKoneksi = H2HDownloadUpload.getKoneksi();
                Statement statetambahHasil = tambahHasilKoneksi.createStatement();
//                String sql = "SELECT * FROM transferins";
                String sql = "SELECT * FROM `java_request_data` WHERE status = 0 ORDER BY id ASC LIMIT 1";
                ResultSet resultambahhasil = statetambahHasil.executeQuery(sql);
                System.out.println("Data Selected JRD        "+date);
//                System.out.println(r.getArray(sql));
                if (sql==null) {
                    System.out.println("Data Kosong");
                } else {
                        while(resultambahhasil.next()){
                        Object[] o = new Object[4];
                        o [0] = resultambahhasil.getString("id");
                        o [1] = resultambahhasil.getString("id_respon");
                        o [2] = resultambahhasil.getString("status");
                        o [3] = resultambahhasil.getDate("waktu_respon");

                        try {
                            File myObj = new File(joutput+""+o[1]+".txt");
                            Scanner myReader = new Scanner(myObj);
                            while (myReader.hasNextLine()) {
                              jsontext = myReader.nextLine();
                              System.out.println("ID : "+o[1]+" Json : "+jsontext+" "+date);
                            }
                            myReader.close();
                            try {

                                String sqlu = "INSERT INTO `java_result_datas` (`id`, `id_data`, `data`, `created_at`) "
                                        + "VALUES (NULL, '"+o[1]+"', '"+jsontext+"', current_timestamp());";
                                PreparedStatement updateTH = tambahHasilKoneksi.prepareStatement(sqlu);

                                updateTH.executeUpdate();
                                updateTH.close();
                                
                                try {
                                    String sql_jreq_data = "UPDATE `java_request_data` SET `status` = '1' "
                                                            + "WHERE `java_request_data`.`id_respon` = "+o[1];
                                    PreparedStatement update_sql_jreq_data = tambahHasilKoneksi.prepareStatement(sql_jreq_data);
                                    update_sql_jreq_data.executeUpdate();
                                    update_sql_jreq_data.close();
                                } catch (Exception e) {
                                }
                            } catch (SQLException e) {
                                System.out.println("Eror "+e+" "+date);
                            }
                        } catch (IOException e) {
                            System.out.println("An error occurred. "+e+" "+date);
//                            e.printStackTrace();
                        }
                    }
                }
                resultambahhasil.close();
                statetambahHasil.close();
            } catch (SQLException e) {
                System.out.println("Eror "+e+" "+date);
            }
            return koneksi;
        }
        //
    public static Connection addJava_request_data(String id){
        java.util.Date date = new java.util.Date(); 
        try {
            Connection konek = H2HDownloadUpload.getKoneksi();
            String querry = "INSERT INTO `java_request_data` "
                    + "(`id`, `id_respon`, `status`, `waktu_respon`) "
                    + "VALUES (NULL, '"+id+"', '0', current_timestamp())";
            PreparedStatement compile = konek.prepareStatement(querry);
            compile.executeUpdate();
            compile.close();
            System.out.println("Data "+id+" Berhasil di tambahkan "+date);
        } catch (Exception e) {
        }
        return koneksi;
    }
    public static void bacafile(){
        //baca file menggunakan filepath
         try {

                List<String> files = findFiles(Paths.get(joutput), "txt");
                files.forEach(x -> jsonread(x));
                
                } catch (IOException e) {
                    e.printStackTrace();
            }
    }
    
    public static String hapusteksLokasi(String tmp){
        //mengambil id dari nama file dan menghapus lokasi dan ekstensi
           String tmp1,tmp2,tmp3;
           tmp1 = tmp;
           tmp2= tmp1.replace(joutput, "");
           tmp3 = tmp2.replace(".txt", "");
        return tmp3;
    }
    public static String jsonread(String x){
        JSONParser parser = new JSONParser();
//            System.out.println(hapusteksLokasi(x));

            try {

              Object obj = parser.parse(new FileReader(x));

              JSONObject jsonObject = (JSONObject) obj;

              String fjurl = (String) jsonObject.get("url");
              jurl = fjurl;
              String fjdb = (String) jsonObject.get("db");
              jdb = fjdb;
              String fjuser = (String) jsonObject.get("user");
              juser = fjuser;
              String fjpass = (String) jsonObject.get("password");
              jpassword = fjpass;
              String fjinput = (String) jsonObject.get("input");
              jinput = fjinput;
              String fjoutput = (String) jsonObject.get("output");
              joutput = fjoutput;
              
                System.out.println("Setting Load Success");
                System.out.println("Connected to url : "+jurl+" DB : "+jdb);
                System.out.println("Path Connected input : "+jinput+" \nOutput : "+joutput);
            } catch (FileNotFoundException ex) {
               ex.printStackTrace();
            } catch (IOException ex) {
               ex.printStackTrace();
            } catch (ParseException ex) {
               ex.printStackTrace();
            }
//            pindahFile(x);
            return x;
    }
    
    public static List<String> findFiles(Path path, String fileExtension)
            //pengecek file path 
        throws IOException {

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }

        List<String> result;

        try (Stream<Path> walk = Files.walk(path)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    // this is a path, not string,
                    // this only test if path end with a certain path
                    //.filter(p -> p.endsWith(fileExtension))
                    // convert path to string first
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith(fileExtension))
                    .collect(Collectors.toList());
        }
  
        return result;
    }
    
    public static String pindahFile(String p){
            
           String nama = hapusteksLokasi(p);
           try {   
            Path temp = Files.move(Paths.get(p),Paths.get(joutput+""+nama+".txt"),StandardCopyOption.REPLACE_EXISTING);
            
            if(temp != null)
            {
                System.out.println("File renamed and moved successfully");
            }
            else
            {
                System.out.println("Failed to move the file");
            }
        } catch (IOException e) {
               System.out.println(e);
        }
        return p;
    }   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        jsonread("setting.json");
        loadSshSettingsFromJson("setting.json");
        try {
            while(loop) {
            long millis = System.currentTimeMillis();
            //code to run
            getdatanow();
//            Thread.sleep(1000 - millis % 1000);
            tambahHasil();
            }//While
        } catch (Exception e) {
            System.out.println(e);
        }      
    }
    
}
