package com.sample.assessment.filescanner;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.IBinder;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerService extends Service {

    public static ArrayList<File> listOfFiles;
    public static ArrayList<String> fileNames;
    public static ArrayList<Long> fileSizes;
    public static HashMap<String,Integer> fileExtensionsCount= new HashMap<String, Integer>();
    public static HashMap<String,Integer> requiredFileExtensions= new HashMap<>();

    final static String MY_ACTION = "MY_ACTION";
    public static Long averageSize=0L;
    public static Long totalSizeOfFiles;

    public ScannerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File[] list=sdCardRoot.listFiles();

        if(list.length == 0)
        getFiles(list);
        Collections.sort(listOfFiles,Collections.reverseOrder());
        fileNames= new ArrayList<>();
        fileSizes= new ArrayList<>();
        for(int i=0;i<10;i++){
            fileNames.add(listOfFiles.get(i).getName());
            fileSizes.add(listOfFiles.get(i).length());
        }
        averageSize = totalSizeOfFiles/listOfFiles.size();

        for(String s : fileNames){
            String extension = s.substring(s.indexOf(".")+1);

            int count=0;
            for(String s1 : fileNames){
                if(s1.contains(extension)){
                     count++;
                }
            }
            fileExtensionsCount.put(extension,count);
        }

        List<Map.Entry<String,Integer>> x=entriesSortedByValues(fileExtensionsCount);

        int i =0;
        for (Map.Entry<String,Integer> entry:x){

            if(i<5) {
                requiredFileExtensions.put(entry.getKey(), entry.getValue());
                i++;
            }
        }

        Intent intent1 = new Intent();
        intent1.setAction(MY_ACTION);
        intent1.putExtra("requiredFileExtensions",requiredFileExtensions);
        intent1.putExtra("fileNames",fileNames);
        intent1.putExtra("fileSizes",fileSizes);
        intent1.putExtra("averageSize",averageSize);
        sendBroadcast(intent1);


        return super.onStartCommand(intent, flags, startId);
    }

    static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<K,V>>() {
                    @Override
                    public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        return e1.getValue().compareTo(e2.getValue());
                    }
                }
        );

        return sortedEntries;
    }

    public void getFiles(File[] list){
        listOfFiles= new ArrayList<>();
        totalSizeOfFiles = 0L;
        for (File f : list) {
            if (f.isDirectory()) {
                getFiles(f.listFiles());
            } else {
                totalSizeOfFiles+=f.length();
                listOfFiles.add(f);
            }
        }
       
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
