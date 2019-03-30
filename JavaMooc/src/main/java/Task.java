import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.alibaba.druid.pool.DruidDataSource;

import javax.xml.parsers.*;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Task implements Runnable {
    Socket socket;
    ArrayList<String> ServerList = new ArrayList<String>();
    ArrayList<String> ClientList = new ArrayList<String>();
    static int N = 0;
    Connection connection;

    Task(Socket socket) {
        this.socket = socket;
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("feidianjava");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test");
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(10);
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            readXML();
            while (true) {
                String Conversation;
                Conversation = bufferedReader.readLine();
                if (Conversation == null) {
                    socket.close();
                    break;
                }

                System.out.println("Conversation:" + System.getProperty("line.separator") + Conversation);

                //遍历server.xml文件中的所有client-server的mapping，检测收到的字符串和client是否一致。
                for (int i = 0; i < ClientList.size(); i++) {
                    if (Conversation.equals(ClientList.get(i)) && Conversation.endsWith(".html")) {
                        System.out.println("Server is sending response...");
                        String response = getServerHtml("E:\\JavaProjects\\JavaMooc\\src\\main\\resources\\" + ServerList.get(i));
                        System.out.println(response);
                        dataOutputStream.writeBytes(response + System.getProperty("line.separator"));
                        N++;
                        writeToDatabase(Conversation);
                    } else if (Conversation.equals(ClientList.get(i)) && Conversation.endsWith(".action")) {
                        System.out.println("Server is acting...");
                        String response = getAction("java E:\\JavaProjects\\JavaMooc\\src\\main\\java\\" + ServerList.get(i));
                        dataOutputStream.writeBytes(response + System.getProperty("line.separator"));
                        System.out.println(response);
                        N++;
                        writeToDatabase(Conversation);
                    }
                }
                dataOutputStream.writeBytes("Over" + System.getProperty("line.separator"));
                System.out.println("Server: Done!");
            }
            System.out.println("Server is Over");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //向数据库中添加数据。
    private void writeToDatabase(String Conversation) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            PreparedStatement preparedStatement;
            String sql = "insert into client_access (ip,ip_address,access_time,parameters) values (?,?,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, N);
            preparedStatement.setString(2, socket.getLocalAddress().toString());
            preparedStatement.setString(3, simpleDateFormat.format(new Date()));
            preparedStatement.setString(4, Conversation);
            preparedStatement.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //从server.xml文件中读取数据
    private void readXML() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("server.xml");

            NodeList file_mapping = document.getChildNodes();//获取到所有的file_mapping 长度为 1
            for (int i = 0; i < file_mapping.getLength(); i++) {
                Node mappings = file_mapping.item(i);
                NodeList mappingList = mappings.getChildNodes();// 拿到file_mapping结点,算上空白,个数为5
                //疑问:getChildNodes和.item()区别是?

                for (int j = 0; j < mappingList.getLength(); j++) {
                    Node mapping = mappingList.item(j);
                    if (mapping.getNodeType() == Node.ELEMENT_NODE) {
                        NodeList contentList = mapping.getChildNodes();

                        for (int k = 0; k < contentList.getLength(); k++) {
                            Node meta = contentList.item(k);
                            if (meta.getNodeType() == Node.ELEMENT_NODE) {
                                if (contentList.item(k).getNodeName().equals("client")) {
                                    ClientList.add(contentList.item(k).getTextContent());
                                } else if (contentList.item(k).getNodeName().equals("server")) {
                                    ServerList.add(contentList.item(k).getTextContent());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //向客户端传入相应html内的字符串
    private String getServerHtml(String command) {
        String response = "";
        String Line;
        try {
            InputStream inputStream = new FileInputStream(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((Line = bufferedReader.readLine()) != null) {
                response += Line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    //向客户端传入运行相应.class文件后的输出结果
    private String getAction(String command) {
        String response = "";
        try {
            Process p = Runtime.getRuntime().exec(command);
            InputStream inputStream = p.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String Line;
            while ((Line = bufferedReader.readLine()) != null) {
                response += Line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
