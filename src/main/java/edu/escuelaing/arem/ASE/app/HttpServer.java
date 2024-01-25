package edu.escuelaing.arem.ASE.app;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class HttpServer {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static String GET_URL = "http://www.omdbapi.com/?apikey=414f88f2&t=";

    private static boolean askingMovie;

    private static ConcurrentHashMap<String, String> moviesSearched = new ConcurrentHashMap<>();;


    /**
     * Called when a client is asking a movie, to show the data of it
     * @param resource the resource requested in the URL
     * @return the output which contains the movie´s data requested
     * @throws IOException
     */
    public static String movieDataService(String resource) throws IOException {

        String movieName = resource.split("t=")[1];

        String movieData, outputLine;

        // Checks if the hashmap already has the movie
        if (moviesSearched.containsKey(movieName)) {
            movieData = moviesSearched.get(movieName);
        } else {
            movieData = connectionWithExternalRestApi(movieName);
            moviesSearched.put(movieName, movieData);
        }

        // If the movie does not exist
        if (movieData.contains("Movie not found")) {
            outputLine = "HTTP/1.1 400 Bad Request\r\n"
                    + "Content-Type:text/html, application/json; charset=utf-8\r\n";
        } else {
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type:text/html, application/json; charset=utf-8\r\n"
                    + "\r\n"
                    + "<!DOCTYPE html>\r\n" + //
                    "<html>\r\n" + //
                    "    <head>\r\n" + //
                    "        <title>" + movieName + "</title>\r\n" + //
                    "        <meta charset=\"UTF-8\">\r\n" + //
                    "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                    "    </head>\r\n" + //
                    "    <body>\r\n" +
                    "        <h2>Movie details</h2>\r\n" + //
                    "        <table id=\"movieTable\" border=\"1\">\r\n" + //
                    "        </table> \r\n" + //
                    buildTable(movieData) +
                    "    </body>\r\n" + //
                    "</html>";
        }

        return outputLine;
    }

    /**
     * Connection with external REST API
     * @param movieName Name of the movie to be asked
     * @return movie's data
     * @throws IOException
     */
    public static String connectionWithExternalRestApi(String movieName) throws IOException{
        URL obj = new URL(GET_URL + movieName);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        StringBuffer response = null;

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println("GET DONE");
        return response.toString();
    }

    /**
     * Builds the table to represent the data of the movie
     * @param movieData the movie's information
     * @return
     */
    private static String buildTable(String movieData) {
        String rows;
        rows = "<script>\n" +
                "    var movieData = " + movieData + "\r\n" +
                "    // Function to add a row\r\n" +
                "    function addRow(table, key, value) {\r\n" +
                "        var row = table.insertRow();\r\n" +
                "        var cell1 = row.insertCell(0);\r\n" +
                "        var cell2 = row.insertCell(1);\r\n" +
                "        cell1.innerHTML = key;\r\n" +
                "   // case of \"Ratings\"\n" +
                "        if (key === \"Ratings\" && Array.isArray(value)) {\n" +
                "           var ratingsHTML = \"<ul>\";\n" +
                "           value.forEach(function(rating) {\n" +
                "               ratingsHTML += \"<li>\" + rating.Source + \": \" + rating.Value + \"</li>\";\n" +
                "           });\n" +
                "           ratingsHTML += \"</ul>\";\n" +
                "           cell2.innerHTML = ratingsHTML;\n" +
                "        } else {\n" +
                "           cell2.innerHTML = value;\n" +
                "        }" +
                "    }\r\n" +
                "\r\n" +
                "    // Get the table\r\n" +
                "    var table = document.getElementById(\"movieTable\");\r\n" +
                "\r\n" +
                "    // Add rows to the table\r\n" +
                "    for (var property in movieData) {\r\n" +
                "        if (movieData.hasOwnProperty(property)) {\r\n" +
                "            addRow(table, property, movieData[property]);\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "</script>";
        return rows;
    }

    public ConcurrentHashMap<String, String> getMoviesSearched() {
        return moviesSearched;
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        try {

            // Starts hearing on 35000 port
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {

            askingMovie = false;
            Socket clientSocket = null;

            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            // Sets up the out and in streams
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine = null;
            int count = 0;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (count == 0 && inputLine.contains("/movie")) {
                    System.out.println("again");
                    askingMovie = true;
                    String resource = inputLine.split(" ")[1];
                    outputLine = movieDataService(resource);
                    out.println(outputLine);
                }

                if (!in.ready()) {
                    break;
                }
                count ++;
            }

            if (!askingMovie) {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type:text/html; charset=utf-8\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>\r\n" + //
                        "<html>\r\n" + //
                        "    <head>\r\n" + //
                        "        <title>Ask movie</title>\r\n" + //
                        "        <meta charset=\"UTF-8\">\r\n" + //
                        "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                        "    </head>\r\n" + //
                        "    <body>\r\n" + //
                        "        <h1>Ask a movie!</h1>\r\n" + //
                        "        <form action=\"/movie\">\r\n" + //
                        "            <label for=\"movie\">Please, type the movie's name you want to know about: </label><br>\r\n" + //
                        "            <input type=\"text\" id=\"movie\" name=\"t\"><br><br>\r\n" + //
                        "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\r\n" + //
                        "        </form> \r\n" + //
                        "        <script>\r\n" + //
                        "            function loadGetMsg() {\r\n" + //
                        "                let movieTitle = encodeURIComponent(document.getElementById(\"movie\").value).replace(/%20/g, \"+\");\r\n" + //
                        "                console.log(movieTitle);\r\n" + //
                        "                const xhttp = new XMLHttpRequest();\r\n" + //
                        "                xhttp.onload = function() {\r\n" +
                        "                   console.log(this.status);\r\n" + //
                        "                   if (this.status === 200) {\r\n" + //
                        "                       window.location.href = \"/movie?t=\"+movieTitle;\r\n" + //
                        "                   } else {\r\n" +
                        "                       alert(\"The movie \"+ movieTitle.replaceAll(\"\\+\", \" \") + \" does not exist.\");\r\n" +
                        "                   }\r\n" + //
                        "                }\r\n" + //
                        "                xhttp.open(\"GET\", \"/movie?t=\"+movieTitle);\r\n" + //
                        "                xhttp.send();\r\n" + //
                        "            }\r\n" + //
                        "        </script>\r\n" + //
                        "\r\n" + //
                        "    </body>\r\n" + //
                        "</html>";
                out.println(outputLine);
            }

            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}