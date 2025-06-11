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
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                String firstMessage = in.readLine();
                if (firstMessage != null && firstMessage.startsWith("USERNAME:")) {
                    username = firstMessage.substring(9).trim();
                } else {
                    username = "Desconhecido";
                }

                synchronized (clients) {
                    clients.add(out);
                    for (PrintWriter writer : clients) {
                        writer.println(username + " entrou no chat.");
                    }
                }



                String message;
                while ((message = in.readLine()) != null) {
                    String fullMessage = username + ": " + message;
                    synchronized (clients) {
                        for (PrintWriter writer : clients) {
                            writer.println(fullMessage);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro com usuário: " + username);
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
                synchronized (clients) {
                    clients.remove(out);
                    for (PrintWriter writer : clients) {
                        writer.println(username + " saiu do chat.");
                    }
                }
            }
        }
    }
}
