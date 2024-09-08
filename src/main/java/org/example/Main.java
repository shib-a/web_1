package org.example;

import com.fastcgi.FCGIInterface;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        var fcgiint = new FCGIInterface();
        while (fcgiint.FCGIaccept() >=0){
            var content= """
                    <html>
                    <head><title>hello wrodle</title></head>
                    <body><h1>h,w</h1></body>
                    </html>
                    """;
            var httpResponse = """
                    HTTP/1.1 200 OK
                    Content-Type: text/html
                    Content-Length: %d
                    
                    %s
                    """.formatted(content.getBytes(StandardCharsets.UTF_8).length, content);
            System.out.println(httpResponse);
        }
    }
}