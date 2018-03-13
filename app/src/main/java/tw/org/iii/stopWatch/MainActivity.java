package tw.org.iii.stopWatch;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static boolean isStart;
    private TextView clock;
    private Button right_button,left_button;
    private Timer timer;
    private static int i;
    private  static  MyClockTask c1;
    private static ClockHandler clockHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clock=findViewById(R.id.clock);
        right_button=findViewById(R.id.rihgt_button);
        left_button=findViewById(R.id.left_button);
        timer= new Timer();
        clockHandler =new ClockHandler();
        initClock();
    }
    private void initClock(){
        right_button.setText((!isStart)?"Start":"Stop");
        left_button.setText((!isStart)?"Reset":"Lap");
        clock.setText("00:00:00");
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
        if(c1==null) {
            c1 = new MyClockTask();
            timer.schedule(c1, 10, 10);
        }
    }

    private void doStop() {
        if(c1!=null) {
            c1.cancel();
            c1=null;
        }
    }

    public void doleft(View view) {
        if(!isStart){
            doReset();

        }else{
            doLap();

        }
    }

    private void doLap() {

    }

    private void doReset(){
        initClock();
        i=0;
    }

    private  class MyClockTask extends TimerTask{
        @Override
        public void run() {
            i++;
            Log.v("chad",""+i);
            clockHandler.sendEmptyMessage(0);
        }
    }
    private  class ClockHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clock.setText(i/6000+":"+i/100+":"+i%100);
        }
    }
}
