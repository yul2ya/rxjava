package com.yul2ya.rxjava;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.yul2ya.rxjava.FileManager.deleteFile;

public class ZipFileManager {

    public static boolean unzip(String filePath) {
        try {
            File zipFile = new File(filePath);
            String destinationPath = zipFile.getParentFile().getPath();

            InputStream inStream = new FileInputStream(filePath);
            ZipInputStream zipInStream = new ZipInputStream(new BufferedInputStream(inStream));
            byte[] buffer = new byte[1024];

            ZipEntry zipEntry;
            while ((zipEntry = zipInStream.getNextEntry()) != null) {
                String filename = zipEntry.getName();

                if (zipEntry.isDirectory()) {
                    File directory = new File(destinationPath + "/" + filename);
                    directory.mkdirs();
                    continue;
                }

                FileOutputStream fileOutStream = new FileOutputStream(destinationPath + "/" + filename);

                int numOfReadByte = -1;
                while ((numOfReadByte = zipInStream.read(buffer)) != -1) {
                    fileOutStream.write(buffer, 0, numOfReadByte);
                }

                fileOutStream.close();
                zipInStream.closeEntry();
            }

            zipInStream.close();
            inStream.close();
            deleteFile(zipFile);
        } catch (IOException e) {
            Log.e("ZipFileManager", "unzip is failed, IOException", e);
            return false;
        }

        return true;
    }
}
