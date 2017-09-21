package naiveweb;

/**
 * A simple http server
 */
import java.net.*;
import java.io.*;


public class App
{
    public static void main( String[] args ) throws Exception {
        if (args.length > 1) {
            Logger.error("Usage: java EchoServer [port number]");
            System.exit(1);
        }

        int portNumber = 80;
        if (args.length == 1) portNumber = Integer.parseInt(args[0]);

        try (
            ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            Logger.info("***Server is listening on port ", Integer.toString(portNumber), "...\n\n");
            while(true) {
                try (
                    Socket clientSocket = serverSocket.accept();

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ) {
                    Logger.info("***A connection established");

                    HttpParser parser = new HttpParser();
                    String line;
                    boolean requestDone;
                    do {
                        // \r\n at the end of line were trimmed by underlying lib
                        line = in.readLine();
                        // Logger.info(line);

                        // Logger.info("Hex: " + Arrays.toString(line.getBytes()));
                        requestDone = parser.parseRequestMessage(line);
                    } while (!requestDone);

                    Logger.info("***Header ends here");
                    parser.dumpRequestInfo();

                    HttpResponse response = new HttpResponse(out, parser.getUri());
                    response.respond();
                } catch (IOException e) {
                    Logger.error("Exception caught when trying to listen on port ", Integer.toString(portNumber),
                        " or listening for a connection");
                    Logger.error(e.getMessage());
                    System.exit(1);
                }
                Logger.info("***A socket closed...\n");
            }
        } catch (IOException e) {
            Logger.error("IOException: ", e.getMessage());
        }
    }
}
