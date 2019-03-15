package com.yul2ya.rxjava;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlConnectionTest {

    @Test
    void test() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:3000/upload").openConnection();
        OutputStream out = getOutputStream(connection);
        DataOutputStream outputStream = new DataOutputStream(out);

        URL resource = getClass().getResource("20180913_seowoo.jpg");

        FileInputStream inputStream = new FileInputStream(resource.getFile());

        byte[] buffer = new byte[1024 * 8];
        int readSize;
        while ((readSize = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, readSize);
        }

        System.out.println(connection.getResponseCode());
        System.out.println(connection.getResponseMessage());

        try (InputStream in = connection.getInputStream();
             ByteArrayOutputStream tempOut = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while ((length = in.read(buf)) != -1) {
                tempOut.write(buf, 0, length);
            }
            System.out.println(new String(tempOut.toByteArray(), "UTF-8"));
        }


        outputStream.close();
    }

    private OutputStream getOutputStream(HttpURLConnection connection) throws IOException {
        //HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:3000/upload").openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Content-Type", "image/jpeg");

        return connection.getOutputStream();
    }
}
