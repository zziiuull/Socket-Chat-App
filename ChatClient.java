import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Digite seu nome: ");
        String username = userInput.readLine();
        output.println("USERNAME:" + username);

        new Thread(() -> {
            try {
                String msg;
                while((msg = input.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException e) {
                System.out.println("Conexão encerrada.");
            }
        }).start();

       String userMsg;
        while ((userMsg = userInput.readLine()) != null) {
            output.println(userMsg);
        }
    }
}
