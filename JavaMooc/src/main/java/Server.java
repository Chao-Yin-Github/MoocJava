import java.net.*;
import java.util.concurrent.*;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8001);
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(8);
        while(true) {
            Socket socket = serverSocket.accept();
            System.out.println("A guest comes in!");
            Task task = new Task(socket);
            executor.execute(task);
        }
    }
}
