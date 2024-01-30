_See https://c0lrm3r.github.io/cse15l-lab-reports/labs/week3_report_  
_2024-01-29_  
_CSE 15L Lab_  
_Week 3 Report_  

# Week 3 Lab Report

I would like to clarify, since the graders of the last report weren't sure, that this is indeed a GitHub Pages site. See the link at the top of the report for yourself! I would also like to clarify that I spent way to much time playing with CSS to make it look like this. Was it worth it?

## Part 1 (The Chat Server)

In implementing this chat server, I chose not to use the existing `Server.java` that this class has been working with. This choice was motivated by uncertainty regarding whether using that implementation was allowed, and by simple curiosity. Instead, I _frequently_ referenced it (and the sun HttpServer docs) to build a not quite complete still very obvious clone of its core functionality.

### The Code

```
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
```

#### ChatServer Class

The program is broken in to two classes, the first of which is the `ChatServer` class. It is responsible for creating and configuring the server, as well as storing some global settings (Bad practice I know, but it made more sense than passing everything in to the ChatHandler class for this implementation) as static constants. This class also holds the main method, which acts as the entry point for the server. This is also where the user can enter a port to run the server via the command line.

#### ChatHandler Class

The `ChatHandler` class implements Suns `HttpHandler`, and is responsible for processing incoming requests (as well as formatting and returning output to clients). This class also holds a string representing the log of all previous chats, which new messages are appended to. The _handle_ method accepts an HttpExchange as an argument, and processes it by checking whether its path matches `/add-message` (or whatever it is defined to in the ChatServer class). If there isn't a match, it simply returns the chat log. However, if there is a match, it then checks the queries attached to the URL. The assignment specified that there would always be 2 queries in the same order, but this implementation does not assume that. Instead, it splits the query string and checks each entry, if it matches either of the required queries, it saves that field. That way, the queries could be in any order, as long as the handler is able to parse _both_ query types, it will add the message to the log.  

This implementation is not perfect, as it neglects a lot of input and error checking in favor of simplicity, but it allows for more flexibility in the way that requests are parsed, as well as expandability for future queries that we might want to be able to handle.

### Examples of the Server Running

_Note: The "CHAT STARTED" message is added to the log when the server starts as an indicator, see code above._  

#### Example 1:
![/add-message screenshot 1](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week3/screen.jpg)

In this example, we are requesting the path `/add-message` with query parameters `s=Woah amazing epic chat server` and `user=TAGrader`. When the server receives this request, it is mapped to our custom exchange handler `ChatHandler`. The method `ChatHandler.handle(HttpExchange exchange)` is called with the relevant exchange (which holds both the request received and response to be delivered) as its argument. The handler then extracts the URI from the exchange object, and parses it through a series of conditionals. In this case, since the URI path is "`/add-message`" and has a non-null query, the handler parses it as a message to be added to the log. It splits the query string into fragments, and then parses each fragment accordingly. As a result, the `msg` local variable is set to "Woah amazing epic chat server" and the `user` local variable is set to "TAGrader". Because both variables are populated, the handler calls `ChatHandler.addChatMsg(String msg)` with the concatenated string `"user + ": " + msg"` as its argument, which is then concatenated to the chatLog field. Finally, the private method `ChatHandler.postResponse(HttpExchange, int code, String res)` is called, which handles populating the exchange's response fields and sends the response to the client.

#### Example 2:
![/add-message screenshot 1](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week3/screen2.jpg)

Similar to the previous example, this shows a request to the `add-message` path with query fragments `s=Yea I know Right` and `user=Me`. The custom exchange handler extracts the URI, checks the path, then extracts the fragments as an array and iterates over them. This allows for the handler to process fragments regardless of the order in which they appear in the array. First, the handler finds the `s=Yea I know Right` fragment, splits it at the equals, and copies it to the `msg` variable. Then it does the same with the `user=Me` and the `user` variable. Once it has fully parsed the URL, it concatenates the formatted message to the chat log using `ChatHandler.addChatMsg(String msg)`, and posts the response back to the client with `ChatHandler.postResponse(HttpExchange, int code, String res)`. The exchange argument, like the previous example, is passed in from the handler's exchange parameter, the response code (200) comes from the HTTP response status codes [See docs](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/200), and the string is simply the `chatLog` field.

## Part 2 (SSH)

After generating a key pair, the private key is stored locally on my machine at `/home/thinkpad/.ssh/id_rsa`. See below:

```
thinkpad@thinkpad:~$ cd /home/thinkpad/.ssh/
thinkpad@thinkpad:~/.ssh$ ls -l
total 16
-rw------- 1 thinkpad thinkpad 2602 Jan 24 15:01 id_rsa
-rw-r--r-- 1 thinkpad thinkpad  571 Jan 24 15:01 id_rsa.pub
-rw------- 1 thinkpad thinkpad 2432 Jan 17 15:15 known_hosts
-rw------- 1 thinkpad thinkpad 1012 Aug 15 21:45 known_hosts.old
thinkpad@thinkpad:~/.ssh$ ls id_rsa
id_rsa
thinkpad@thinkpad:~/.ssh$
```
Note that there is still a local copy of `id_rsa.pub` (the public key) on my machine, it doesn't go away when you copy it.  

On the remote `ieng6.ucsd.edu` machine, my public key is stored in the `/home/linux/ieng6/oce/59/colarmer/.ssh/authorized_keys` file of my user account. See below:

```
[colarmer@ieng6-201]:~:52$ cd ~/.ssh/
[colarmer@ieng6-201]:.ssh:53$ ls
authorized_keys
[colarmer@ieng6-201]:.ssh:54$ pwd
/home/linux/ieng6/oce/59/colarmer/.ssh
[colarmer@ieng6-201]:.ssh:55$
```

Note that the file name `authorized_keys` is different than the `id_rsa.pub` on my local machine. This is the file it was copied in to when setting up key-pair authentication. `ieng6.ucsd.edu` has marked my public key as trustworthy, and will use it when my machine attempts to connect to the server.

Now when connecting to the cluster over SSH, authentication is automatically done using this key pair. The result is simple, password-less authentication:

!(SSH connection without password)[https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week3/screen4.jpg]

## Part 3 (Reflection)

Full disclosure, I'm not new to linux or the command line. I've previously used all the commands that we have explored, and I am familiar with SSH, GitHub, Markdown, and public-private key authentication. Despite this, I honestly had no clue that `cat`, when supplied no arguments, echoes STDIN. Furthermore, I had never used Java to implement an HttpServer, and the packages that it provides to do so were new to me. Overall, these labs have been strengthening my knowledge of all these subjects, even if I am already familiar with most of them.