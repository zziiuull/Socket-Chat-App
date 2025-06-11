import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private  static final int PORT = 12345;
    private static Set<PrintWriter> clients = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta " + PORT);

        while(true) {
            Socket clientSocket = serverSocket.accept();
            new ClientHandler(clientSocket).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clients) {
                    clients.add(out);
                }

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    synchronized (clients) {
                        for (PrintWriter writer : clients) {
                            writer.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
                synchronized (clients) {
                    clients.remove(out);
                }
            }
        }
    }
}
