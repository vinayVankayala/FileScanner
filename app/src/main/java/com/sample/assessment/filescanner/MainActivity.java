package com.sample.assessment.filescanner;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button start,stop;
    FloatingActionButton floatBtn;
    TextView fileExtension,fileSize,extensionFrequency;
    MyReceiver myReceiver;
    public static final int MY_PERMISSIONS_REQUEST_CONTACTS=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatBtn = (FloatingActionButton) findViewById(R.id.fab);

        floatBtn.setVisibility(View.GONE);
        start = (Button)findViewById(R.id.start);
        stop = (Button)findViewById(R.id.stop);
        extensionFrequency = (TextView) findViewById(R.id.extensionFrequency);
        extensionFrequency.setMovementMethod(new ScrollingMovementMethod());
        fileSize = (TextView) findViewById(R.id.fileSize);
        fileExtension = (TextView) findViewById(R.id.fileExtension);
        fileExtension.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    protected void onStart() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScannerService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
        start.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {


                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CONTACTS);
                }

        }
    });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),ScannerService.class);
                stopService(intent);
            }
        });
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    private class MyReceiver extends BroadcastReceiver {
    HashMap<String,Long> da ;


        public  ArrayList<String> fileNames;
        public  ArrayList<Long> fileSizes;
        @Override
        public void onReceive(Context arg0, Intent intent) {
            String fileExtensionString="";
            String extensionFrequencyString="";
            Long averageSize=0L;
            NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mgr.cancel(9999);

            da  = (HashMap) intent.getSerializableExtra("z");
            averageSize = intent.getLongExtra("averageSize",Long.valueOf(1));
            fileNames = (ArrayList<String>) intent.getSerializableExtra("fileNames");
            fileSizes = (ArrayList<Long>)intent.getSerializableExtra("fileSizes");

            for(int i=0;i<fileNames.size();i++){
                fileExtensionString +=fileNames.get(i)+":"+fileSizes.get(i);
            }
            fileExtension.setText("");
            fileExtension.setText(fileExtensionString);
            fileSize.setText(averageSize+"");
            for(Map.Entry<String,Long> f1 : da.entrySet()){
                extensionFrequencyString+=f1.getKey()+":"+f1.getValue()+"/n";
            }
            extensionFrequency.setText(extensionFrequencyString);
            floatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });
            floatBtn.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(), ScannerService.class);
                    startService(intent);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new Notification(R.drawable.launcher, "Scanning", System.currentTimeMillis());
                    notificationManager.notify(9999, notification);
                }else if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_CONTACTS);

                    }else {

                        Toast.makeText(this, "Permission to access SD card denied.Exiting.", Toast.LENGTH_LONG).show();
                        MainActivity.this.finish();

                    }
                }
        }
    }
}
