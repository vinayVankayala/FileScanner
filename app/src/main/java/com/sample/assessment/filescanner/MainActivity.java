package com.sample.assessment.filescanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button start,stop;
    TextView fileExtension,fileSize,extensionFrequency;
    MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton sharingButton = new ImageButton(this);
        sharingButton.setLayoutParams(new ViewGroup.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
        sharingButton.setClickable(false);
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
            Intent intent= new Intent(getApplicationContext(),ScannerService.class);
            startService(intent);
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
            String d="";
            String c="";
            Long averageSize=0L;

            da  = (HashMap) intent.getSerializableExtra("z");
            averageSize = intent.getLongExtra("w",Long.valueOf(1));
            fileNames = (ArrayList<String>) intent.getSerializableExtra("x");
            fileSizes = (ArrayList<Long>)intent.getSerializableExtra("y");
            Log.i("kbiwbdoivucbwevoucbweoc","onrecieve d"+d);

            for(int i=0;i<fileNames.size();i++){
                d +=fileNames.get(i)+":"+fileSizes.get(i);
            }
            fileExtension.setText("");
            fileExtension.setText(d);
            fileSize.setText(averageSize+"");
            for(Map.Entry<String,Long> f1 : da.entrySet()){
                c+=f1.getKey()+":"+f1.getValue()+"/n";
            }
            extensionFrequency.setText(c);
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
}