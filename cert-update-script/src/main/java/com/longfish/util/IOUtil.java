package com.longfish.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IOUtil {

    public static void write(String info, String path){
        FileWriter fw = null;
        try {
            File file = new File(path);
            fw = new FileWriter(file);
            fw.write(info);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fw != null;
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
