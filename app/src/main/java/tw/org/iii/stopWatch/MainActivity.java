package tw.org.iii.stopWatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static boolean isStart;
    private TextView clock;
    private Button right_button,left_button;
    private ClockHandler handler;
    private static int i,title;
    private TextToSpeech tts;
    private ClockRecevier recevier;
    private ListView list_view;
    private SimpleAdapter adapter;
    private static ArrayList<HashMap<String,String>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clock=findViewById(R.id.clock);
        list_view=findViewById(R.id.list_view);
        right_button=findViewById(R.id.rihgt_button);
        left_button=findViewById(R.id.left_button);
        createLanguageTTS();
        initClock();
        IntentFilter intentFilter =new IntentFilter();
        intentFilter.addAction("now");
        recevier=new ClockRecevier();
        registerReceiver(recevier,intentFilter);
        handler =new ClockHandler();
        if(data==null) {
            data = new ArrayList<>();
        }
        String[] from =new String[]{"title","context"};
        int[] to =new int[]{R.id.title,R.id.contex};
        adapter = new SimpleAdapter(this,data,R.layout.sample_lis,from,to);
        list_view.setAdapter(adapter);
    }

    private void initClock(){
        right_button.setText((!isStart)?"Start":"Stop");
        left_button.setText((!isStart)?"Reset":"Lap");
        clock.setText(i/6000+":"+(i/100)%60+":"+i%100);
    }
    public void doright(View view) {
        if(!isStart){
            doStart();
            right_button.setText("Stop");
            left_button.setText("Lap");
            isStart=true;
        }else{
            doStop();
            right_button.setText("Start");
            left_button.setText("Reset");
            isStart=false;
        }
    }

    private void doStart() {
        Intent startSverice = new Intent(this, TimerService.class);
        startSverice.putExtra("stat", 0);
        startService(startSverice);
    }

    private void doStop() {
        Intent stopService = new Intent(this,TimerService.class);
        stopService.putExtra("stat",1);
        startService(stopService);
    }

    public void doleft(View view) {
        if(!isStart){
            doReset();

        }else{
            doLap();

        }
    }

    private void doLap() {
        HashMap<String,String> map =new HashMap<>();
        map.put("title",""+(title++));
        map.put("context",""+i);
        data.add(map);
        adapter.notifyDataSetChanged();
    }

    private void doReset(){
        initClock();
        Intent resetService = new Intent(this,TimerService.class);
        resetService.putExtra("stat",2);
        startService(resetService);
        i=0;
        title=0;
        clock.setText(i/6000+":"+(i/100)%60+":"+i%100);
        data.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if(!isStart&&i==0){
            Intent stopService = new Intent(this,TimerService.class);
            stopService.putExtra("stat",3);
            stopService(stopService);
            data.clear();
            data=null;
        }
        unregisterReceiver(recevier);
        tts.shutdown();
        super.onDestroy();
    }
    private void createLanguageTTS(){
        if( tts == null )
        {
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
                @Override
                public void onInit(int arg0)
                {
                    // TTS 初始化成功
                    if( arg0 == TextToSpeech.SUCCESS )
                    {
                        // 指定的語系: 英文(美國)
                        Locale l = Locale.CHINA;  // 不要用 Locale.ENGLISH, 會預設用英文(印度)

                        // 目前指定的【語系+國家】TTS, 已下載離線語音檔, 可以離線發音
                        if( tts.isLanguageAvailable( l ) == TextToSpeech.LANG_COUNTRY_AVAILABLE )
                        {
                            tts.setLanguage( l );
                            //設定說話速度
                            tts.setSpeechRate(4.0f);
                        }
                    }
                }}
            );
        }
    }
    private  class ClockHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle =msg.getData();
            i =bundle.getInt("now");
            clock.setText(i/6000+":"+(i/100)%60+":"+i%100);
            tts.speak(i+"",TextToSpeech.QUEUE_ADD,null);


        }
    }
    private class ClockRecevier extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int  i =intent.getIntExtra("now",0);
            Message message =new Message();
            Bundle bundle =new Bundle();
            bundle.putInt("now",i);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}
