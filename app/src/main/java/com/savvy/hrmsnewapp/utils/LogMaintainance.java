package com.savvy.hrmsnewapp.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by orapc7 on 09-Jan-18.
 */

public class LogMaintainance {

    public static void DeleteLog() {
        try {
            String path = Environment.getExternalStorageDirectory() + "//SavvyHRMS//Logs";

            File file = new File(path);
            if (file.exists()) {

                File[] listFiles = file.listFiles();
                long purgeTime = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000);
                for (File listFile : listFiles) {
                    if (listFile.lastModified() < purgeTime) {
                        if (!listFile.delete()) {
                            System.err.println("Unable to delete file: " + listFile);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  void WriteLog(String data) {
       /* try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
            String formattedDate = df.format(c.getTime());
            String date = new SimpleDateFormat("yyyyMMdd", Locale.UK).format(new Date());
            data = formattedDate + "->" + data + "\n";
            byte[] bytes = data.getBytes("UTF-8");
            String path = Environment.getExternalStorageDirectory() + "//SavvyHRMS//Logs";

            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = path + "//" + "Log_" + date + ".txt";
            file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(path, true);
            stream.write(bytes);
            stream.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }*/
    }
}
