import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws Exception {
        //开启端口
        Socket clientSocket = new Socket(InetAddress.getByName("127.0.0.1"),8001);
        //开启通道输入流
        InputStream inputStream = clientSocket.getInputStream();
        //增加效率
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        //开启通道输出流
        OutputStream outputStream = clientSocket.getOutputStream();
        //包装一下
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        String send ;
        BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
        while(!(send=keyboardReader.readLine()).equals("")) {
            dataOutputStream.writeBytes(send + System.getProperty("line.separator"));
            System.out.println("Client sends over...");
            String content = "";
            String response;
            while (!(response = bufferedReader.readLine()).equals("Over")) {
                content += response;//+ System.getProperty("line.separator");
                System.out.println(content);
            }
            System.out.println("Complete receiving!" + System.getProperty("line.separator") + content);
            File file = null;
            if(send.equals("hello.html")) {
                file= new File("hello.html");
            }
            else if(send.equals("HelloWorld.action")){
                file = new File ("hello2.html");
            }
            else {
                continue;
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        }
        System.out.println("Client said : I am leaving...");
        clientSocket.close();
    }
}
