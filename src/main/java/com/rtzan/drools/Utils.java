package com.rtzan.drools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.net.URL;


public class Utils {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static String getResourceFilePath(String fileName) {
        URL resource = Utils.class.getClassLoader().getResource(fileName);
        return resource.getPath();
    }

    public static String fileToString(String filePath) {
        try {

            FileInputStream fis = new FileInputStream(filePath);
            return streamToString(fis);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String streamToString(InputStream input) {
        StringWriter sw = new StringWriter();
        copyStream(new InputStreamReader(input), sw);
        return sw.toString();
    }

    public static void copyStream(Reader input, Writer output) {
        final int BUFFER_SIZE = 1024 * 4;
        try {

            char[] buffer = new char[BUFFER_SIZE];
            int len = 0;
            while (-1 != (len = input.read(buffer))) {
                output.write(buffer, 0, len);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);

        } finally {
            try {
                input.close();
            } catch (IOException e) {
                // swallow the exception
            }
            try {
                output.close();
            } catch (IOException e) {
                // swallow the exception
            }
        }
    }
}
