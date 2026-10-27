import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/** Small local web server: serves the Cognition UI and writes learner registrations to Oracle. */
public class CognitionWebServer {
  static String url = System.getenv().getOrDefault("ORACLE_URL", "jdbc:oracle:thin:@//localhost:1521/FREEPDB1");
  static String user = System.getenv("ORACLE_USER"), password = System.getenv("ORACLE_PASSWORD");
  static Connection connect() throws SQLException { return DriverManager.getConnection(url, user, password); }
  static void reply(HttpExchange x, int code, String type, String body) throws IOException {
    byte[] b = body.getBytes(StandardCharsets.UTF_8); x.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
    x.getResponseHeaders().set("Cache-Control", "no-store"); x.sendResponseHeaders(code, b.length); try(OutputStream o=x.getResponseBody()){o.write(b);}
  }
  static String json(String s) { return "\"" + s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r") + "\""; }
  static Map<String,String> form(String body) {
    Map<String,String> m=new HashMap<>(); for(String pair:body.split("&")){String[] p=pair.split("=",2); try {m.put(URLDecoder.decode(p[0],"UTF-8"), URLDecoder.decode(p.length>1?p[1]:"","UTF-8"));} catch(Exception ignored){} } return m;
  }
  static void api(HttpExchange x) throws IOException {
    String method=x.getRequestMethod();
    try {
      if (x.getRequestURI().getPath().equals("/api/learners") && method.equals("GET")) {
        StringBuilder out=new StringBuilder("["); boolean first=true;
        try(Connection c=connect(); PreparedStatement ps=c.prepareStatement("SELECT learner_id, participant_code, display_name, active_flag FROM learner ORDER BY learner_id"); ResultSet rs=ps.executeQuery()) {
          while(rs.next()){if(!first)out.append(',');first=false;out.append("{\"id\":").append(rs.getLong(1)).append(",\"code\":").append(json(rs.getString(2))).append(",\"name\":").append(json(rs.getString(3)==null?"":rs.getString(3))).append(",\"active\":").append(json(rs.getString(4))).append('}');}
        }
        out.append(']'); reply(x,200,"application/json",out.toString()); return;
      }
      if (x.getRequestURI().getPath().equals("/api/learners") && method.equals("POST")) {
        StringBuilder body=new StringBuilder(); try(BufferedReader r=new BufferedReader(new InputStreamReader(x.getRequestBody(),StandardCharsets.UTF_8))){String line;while((line=r.readLine())!=null)body.append(line);}
        Map<String,String> f=form(body.toString()); String code=f.getOrDefault("code","").trim().toUpperCase(Locale.ROOT), name=f.getOrDefault("name","").trim();
        if(code.isEmpty()||name.isEmpty()){reply(x,400,"application/json","{\"error\":\"Participant code and display name are required.\"}");return;}
        try(Connection c=connect(); PreparedStatement ps=c.prepareStatement("INSERT INTO learner (participant_code, display_name) VALUES (?, ?)", new String[]{"LEARNER_ID"})) {
          ps.setString(1,code); ps.setString(2,name); ps.executeUpdate(); c.commit();
        }
        reply(x,201,"application/json","{\"message\":\"Learner saved to Oracle.\",\"code\":"+json(code)+"}"); return;
      }
      reply(x,404,"application/json","{\"error\":\"Not found\"}");
    } catch(SQLException e) {
      String msg=e.getErrorCode()==1?"That participant code already exists.":"Oracle error ("+e.getErrorCode()+"): "+e.getMessage();
      reply(x,400,"application/json","{\"error\":"+json(msg)+"}");
    }
  }
  public static void main(String[] args) throws Exception {
    if(user==null||password==null){System.err.println("Set ORACLE_USER and ORACLE_PASSWORD first.");System.exit(1);}
    try(Connection c=connect()){System.out.println("Connected to Cognition Oracle database.");}
    Path root=Paths.get("web").toAbsolutePath().normalize();
    HttpServer server=HttpServer.create(new InetSocketAddress("127.0.0.1",8080),0);
    server.createContext("/api/", CognitionWebServer::api);
    server.createContext("/", x->{try{String p=x.getRequestURI().getPath(); if(p.equals("/"))p="/index.html";Path file=root.resolve(p.substring(1)).normalize();if(!file.startsWith(root)||!Files.isRegularFile(file)){reply(x,404,"text/plain","Not found");return;}String ct=p.endsWith(".html")?"text/html":p.endsWith(".js")?"text/javascript":"application/octet-stream";reply(x,200,ct,Files.readString(file));}catch(Exception e){reply(x,500,"text/plain","Web server error: "+e.getMessage());}});
    server.start();System.out.println("Open http://localhost:8080 in your browser. Keep this terminal open.");
  }
}
