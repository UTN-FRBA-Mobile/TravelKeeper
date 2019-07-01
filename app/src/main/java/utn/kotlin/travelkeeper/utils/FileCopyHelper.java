package utn.kotlin.travelkeeper.utils;

import java.io.*;

public class FileCopyHelper {

    public static void copyFile(InputStream input, FileOutputStream output) throws IOException {

        try {
            byte[] buffer = new byte[4 * 1024]; // or other buffer size
            int read;

            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            output.flush();
        } finally {
            output.close();
        }
        input.close();

    }
}