package org.example;

import com.fastcgi.FCGIInterface;

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
    static String body = null;
    static String httpResponse = """
                    Content-Type: application/json
                    ContentLength: %d
                    
                    
                    %s
                    """;
    public static void main(String[] args) {
//        test(-3,-5,1);
        var fcgiInt = new FCGIInterface();
        while (fcgiInt.FCGIaccept() >=0){
            try {
//                var res = "{\"result\":"+validate(readRequestBody())+",\"time\":"+LocalDateTime.now()+",\"respTime:\":"+(end-start)+"}";
                var res = "{\"result\":"+validate(readRequestBody()+"}");
                send(res);
            }catch (Exception e){
                var msg = "{\"error\":\""+ e.getMessage()+ " "+ body +"\"}";
                send(msg);
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
        body = new String(requestBody,StandardCharsets.UTF_8);
        return body;
    }

    public static boolean validate(String requestBody){
//        start = System.nanoTime();
        var jo = requestBody.split("&");
        HashMap<String, Double> data = new HashMap<>();
        for (String kv: jo){
            var kvarr = kv.split("=");
            try{
                data.put(kvarr[0],Double.parseDouble(kvarr[1]));
            }catch (NumberFormatException e){data.put(kvarr[0], null);}
        }var x = data.get("x_data");
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
    public static void send(String msg){
        System.out.println(String.format(httpResponse, msg.getBytes().length, msg));
    }


}