package org.example;

import com.fastcgi.FCGIInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;

public class Main {
    static long start;
    static long end;
    public static void main(String[] args) {
//        test(-3,-5,1);
        var fcgiInt = new FCGIInterface();
        while (fcgiInt.FCGIaccept() >=0){
            var httpResponse = """
                    Content-Type: application/json
                    ContentLength: %d
                    
                    
                    %s
                    """;
            try {
//                var res = "{\"result\":"+validate(readRequestBody())+",\"time\":"+LocalDateTime.now()+",\"respTime:\":"+(end-start)+"}";
                var res = String.format("{\"result\": %b}",validate(readRequestBody()));

//                System.out.println(String.format(httpResponse,res.getBytes(StandardCharsets.UTF_8).length,res));
                System.out.println(String.format(httpResponse, res.getBytes(StandardCharsets.UTF_8).length, res));

            }catch (Exception e){
                httpResponse = """
                    Content-Type: text/json
                    ContentLength: %d
                    
                    
                    %s
                    """;
                var msg = "{\"error\": %s}".formatted(e.getMessage());
                System.out.println(httpResponse.formatted(msg.getBytes(StandardCharsets.UTF_8), msg));
            }

        }
    }
    public static String readRequestBody() throws IOException {
        FCGIInterface.request.inStream.fill();
        var contentLength = FCGIInterface.request.inStream.available();
        var buffer = ByteBuffer.allocate(contentLength);
        var readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);
        var requestBody = new byte[readBytes];
        buffer.get(requestBody);
        buffer.clear();
//        var req_str = new String(requestBody, StandardCharsets.UTF_8);
        return new String(requestBody, StandardCharsets.UTF_8);
    }
    public static Boolean validate(String requestBody){
//        start = System.nanoTime();
        var jo = requestBody.split("&");
        HashMap<String, Double> data = new HashMap<>();
        for (String kv: jo){
            var kvarr = kv.split("=");
            try{
                data.put(kvarr[0],Double.parseDouble(kvarr[1]));
            }catch (NumberFormatException e){data.put(kvarr[0], null);}
        }
//        var data = (JsonObject) JsonParser.parseString(g.toJson(jo));
        var x = data.get("x_data");
        var y = data.get("y_data");
        var r = data.get("r_data");
//        end = System.nanoTime();
        return checkCircle(x, y, r) || checkRectangle(x, y, r) || checkTriangle(x, y, r);
    }
    public static boolean checkCircle(double x, double y, double r){
        return (Math.pow(x, 2) + Math.pow(y,2) < Math.pow(r,2) && x>=0 && y>=0);
    }
    public static boolean checkRectangle(double x, double y, double r){
        return (y>-r && x<r/2 && x>=0 && y<=0);
    }
    public static boolean checkTriangle(double x, double y, double r){
        return (x <= 0 && y <= 0 && x > -r/2 && y >= -r && x - y + r >= 0);
    }
    public static void test(double x, double y, double r){
        System.out.println(checkCircle(x, y, r) || checkRectangle(x, y, r) || checkTriangle(x, y, r));
        var res = "{\"result\":"+validate("x_data=0&y_data=0&r_data=5")+",\"time\":"+LocalDateTime.now()+",\"respTime:\":"+((end-start))+"}";
        System.out.println(res);
    }

//    private static String readRequestBody() throws IOException {
//        FCGIInterface.request.inStream.fill();
//
//        var contentLength = FCGIInterface.request.inStream.available();
//        var buffer = ByteBuffer.allocate(contentLength);
//        var readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);
//
//        var requestBodyRaw = new byte[readBytes];
//        buffer.get(requestBodyRaw);
//        buffer.clear();
//
//        return new String(requestBodyRaw, StandardCharsets.UTF_8);
//    }
//
}