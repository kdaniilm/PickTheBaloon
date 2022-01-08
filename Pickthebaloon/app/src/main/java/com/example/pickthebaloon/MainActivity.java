package com.example.pickthebaloon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Timer timer = new Timer();
    private Timer playTimer = new Timer();
    private Random rand = new Random();
    private int score = 0;
    private int period = 1000;
    private int playTime = 0;
    private Boolean isBomb = false;
    private String playTimeString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.recordBtn).setOnClickListener((v) ->{
            setContentView(R.layout.activity_record);
            try {
                ((TextView) findViewById(R.id.recordTextView)).setText(LoadRecord());
            }
            catch (IOException e) {
                SaveRes("best.txt","0", "0");
            }
        });

        findViewById(R.id.startBtn).setOnClickListener((v) -> {
            setContentView(R.layout.activity_field);

            ImageButton target = findViewById(R.id.target);

            target.setOnClickListener(t->
            {
                target.setBackgroundResource(R.drawable.blow);

                if(isBomb == true){
                    timer.cancel();
                    timer = null;

                    SaveRes("best.txt", ((TextView)findViewById(R.id.timeValue)).getText().toString(),
                            ((TextView)findViewById(R.id.scoreValue)).getText().toString());

                    setContentView(R.layout.activity_main);
                }
                else {
                    String text = String.valueOf((score += 10));

                    ((TextView) findViewById(R.id.scoreValue)).setText(text);

                    if(score % 100 == 0 && period > 100){
                        period -= 50;
                    }
                }
            });

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Random rand = new Random();
                    int renderRand = rand.nextInt(100);
                    if(renderRand > 0 && renderRand < 10) {
                        isBomb = true;
                        SetParamsForTarget(target, R.drawable.bomb);
                    }
                    else {
                        isBomb = false;
                        SetParamsForTarget(target, R.drawable.baloon);
                    }

                }
            }, 0, period);
        });
    }

    public void SetParamsForTarget(ImageButton target, int imageId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float scale = getApplicationContext().getResources().getDisplayMetrics().density;

                int width = (int)(100 * scale);
                int height = (int)(100 * scale);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        width,
                        height
                );

                RelativeLayout fieldSize = (RelativeLayout)findViewById(R.id.field);
                int left = rand.nextInt(fieldSize.getWidth() - width);
                int top = rand.nextInt(fieldSize.getHeight() - height);
                params.setMargins(left, top, 0, 0);

                target.setLayoutParams(params);
                target.setBackgroundResource(imageId);
            }
        });
    }

    public void SaveRes(String fileName, String time, String scope){
        try {
            String data = "Time: " + time + "   " + "Score: " + scope;
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(fileName,
                    Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String LoadRecord() throws IOException {
            String result = "";
            InputStream inputStream = openFileInput("best.txt");
            if(inputStream != null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String temp = "";
                StringBuilder stringBuilder = new StringBuilder();

                while((temp = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(temp);
                }

                inputStream.close();
                result = stringBuilder.toString();
            }
            return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SaveRes("continue.txt", ((TextView)findViewById(R.id.timeValue)).getText().toString(),
                ((TextView)findViewById(R.id.scoreValue)).getText().toString());

    }
}