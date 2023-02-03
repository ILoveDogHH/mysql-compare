package com.utils.file;

import java.io.*;

public class FileLog {





    public static void _writeLog(String fileLogPath, String message) throws IOException {
        File file = new File(fileLogPath);
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter writer = null;
        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "UTF-8");
            writer = new BufferedWriter(osw);
            writer.write(message);
        } finally {
            writer.close();
            osw.close();
            fos.close();
        }
    }


    public static String _getLog(String fileLogPath) throws IOException {
        File file = new File(fileLogPath);
        FileInputStream fInput = null;
        InputStreamReader iReader = null;
        BufferedReader bReader = null;
        try {
            String info = "";
            fInput = new FileInputStream(file);
            iReader = new InputStreamReader(fInput, "UTF-8");
            bReader = new BufferedReader(iReader);
            String line = bReader.readLine();
            while (line != null) {
                info = info + line;
                line = bReader.readLine();
            }
            bReader.close();
            return info;
        } finally {
            bReader.close();
            iReader.close();
            fInput.close();
        }
    }

    public static void deleteFile(String filePath){
        File file = new File(filePath);
        if(file.isFile()&&file.exists()){
            file.delete();
        }
    }

}
