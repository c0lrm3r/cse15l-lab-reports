/*
 * Connor Larmer
 * 2024-01-24
 * Simple HTTP-based chat server
 * 
 * Server code adapted from: ucsd-cse15l-f23 wavelet
 * Link: https://github.com/ucsd-cse15l-f23/wavelet/blob/main/Server.java
 */

import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;

public class ChatServer
{
    // Some 'Global' Settings, bad practice? Yea. but eh
    public static final int HTTP_BACKLOG   = 10;
    public static final String BASE_PATH   = "/";
    public static final String MSG_PATH    = "/add-message";
    public static final String QUERY_MSG   = "s";
    public static final String QUERY_USER  = "user";
    public static final String WELCOME_MSG = ":::::::: CHAT STARTED ::::::::";
    public static final String USAGE_HELP  = "Usage: java ChatServer <port>";
 
    private int serverPort;
    private HttpServer server;
    private InetSocketAddress address;
    private ChatHandler chatHandler;

    public ChatServer(int port)
    {
        // Setting up port
        if(port < 0 || port > 65535)
            exitMsg(-1, "Invalid Port!");
        serverPort = port;
        // Creating socket address
        address = new InetSocketAddress(serverPort);
        // Starting server and handler
        chatHandler = new ChatHandler();
        startServer();
    }

    private void startServer()
    {
        // Actually create the server
        try {
            server = HttpServer.create(address, HTTP_BACKLOG);
        } catch(Exception e) {
            if(e.getClass() == BindException.class)
                exitMsg(-1, "ERROR: Failed to bind to address!");
            else
                exitMsg(-1, "Error: An unknown error occured" +
                    " during server creation");
        }
        // Handler
        server.createContext(BASE_PATH, chatHandler);
        server.start();
        System.out.printf("Chat server started, hosting on (%s:%d).\n",
            address.getHostString(), address.getPort());
    }

    private static void exitMsg(int status, String msg)
    {
        System.out.println(msg);
        System.exit(status);
    }

    // Main Method!
    public static void main(String[] args)
    {
        try {
            if(args.length < 1) exitMsg(-1, USAGE_HELP);
            int port = Integer.parseInt(args[0]);
            new ChatServer(port);
        } catch (NumberFormatException e) {
            exitMsg(-1, USAGE_HELP);
        }
    }
}

class ChatHandler implements HttpHandler
{

    private String chatLog;

    public ChatHandler()
    {
        chatLog = new String();
        addChatMsg(ChatServer.WELCOME_MSG);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        try {
            // /add-message?s=<msg>&user=<user>
            URI url = exchange.getRequestURI();
            String msg = null;
            String user = null;

            if(url.getPath().equals(ChatServer.MSG_PATH)
                && url.getQuery() != null)
            {
                String queries[] = url.getQuery().split("&");
                for(String q : queries)
                {
                    String params[] = q.split("=");
                    if(params[0].equals(ChatServer.QUERY_MSG))
                        msg = params[1];
                    if(params[0].equals(ChatServer.QUERY_USER))
                        user = params[1];
                }
                if(msg != null && user != null)
                    addChatMsg(user + ": " + msg);
            }
            postResponse(exchange, 200, chatLog);

        } catch (Exception e) {
            postResponse(exchange, 500, e.toString());
        }
    }

    public void addChatMsg(String msg)
    {
        chatLog += (msg + "\n"); 
    }

    private void postResponse(HttpExchange exchange,
        int code, String res) throws IOException
    {
        exchange.sendResponseHeaders(code, res.getBytes().length);
        OutputStream outStream = exchange.getResponseBody();
        outStream.write(res.getBytes());
        outStream.close();
    }
    
}