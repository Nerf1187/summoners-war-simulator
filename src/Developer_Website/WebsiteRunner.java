package Developer_Website;

import Util.Util.STRINGS;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 * The WebsiteRunner class is responsible for launching the Developer Website (which you can also run via opening index.html through your file explorer)
 *
 * @author Anthony (Tony) Youssef
 * @Note The program will stop hosting on the custom port when refreshing or closing the page.
 *          It is recommended to open the website through your file explorer
 */
public class WebsiteRunner
{
    private record FileHandler(String rootDirectory) implements HttpHandler
        {
            @Override
            public void handle(HttpExchange exchange) throws IOException
            {
                //Get the request path
                String requestPath = exchange.getRequestURI().getPath();
                if (requestPath.equals("/"))
                {
                    requestPath = "/index.html";
                }
                else if (requestPath.endsWith("/quit")) //Quit the program if requested
                {
                    System.exit(0);
                }
                
                //Try and find the requested file
                File file = new File(rootDirectory + requestPath);
                if (!file.exists() || !file.isFile())
                {
                    file = new File(STRINGS.substringUpToString(rootDirectory, "Develop") + requestPath);
                    
                    if (!file.exists() || !file.isFile())
                    {
                        String response = "404 Not Found";
                        exchange.sendResponseHeaders(404, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        return;
                    }
                }
                
                exchange.getResponseHeaders().add("Content-Type", Files.probeContentType(file.toPath()));
                exchange.sendResponseHeaders(200, file.length());
                
                OutputStream os = exchange.getResponseBody();
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[1_024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1)
                {
                    os.write(buffer, 0, bytesRead);
                }
                fis.close();
                os.close();
            }
        }
    
    /**
     * Launches an HTTP server on a specified port (uses 8080) and serves files from a designated directory.
     * <br>
     * - The server binds to the specified port using an instance of {@link HttpServer}.
     * - The base directory for serving files is determined relative to the location of the "index.html" resource.
     * - A context is created for the server to handle requests starting from the root path ("/").
     * - A custom file handler is set to manage requests by serving files from the designated directory.
     * - The server uses a default executor to handle incoming connections.
     * <p>
     * This method outputs the server's address and port to the console and starts the server.
     * If the server fails to initialize, a {@link RuntimeException} is thrown.
     */
    public void main()
    {
        int port = 8080;
        
        //Create the server
        HttpServer server;
        try
        {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        
        String start = STRINGS.substringUpToString(this.getClass().getResource("index.html").getPath(), "out").replaceAll("%20", " ") + "src/Developer_Website";
        
        server.createContext("/", new FileHandler(start));
        server.setExecutor(null);
        
        //Start the server
        server.start();
        System.out.println("Server is running at http://localhost:" + port);
    }
}