package tw.org.iii.stopWatch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {
    private static Timer timer;

    private static boolean isStart;
    private int stat;
    private static int i;
    private static MyClockTask mt ;
    public TimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();




    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer.purge();
        timer =null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null) {
            stat = intent.getIntExtra("stat", 0);
            switch (stat) {
                case 0:
                    doStart();
                    break;
                case 1:
                    doStop();
                    break;
                case 2:
                    doReset();
                    break;
                case 3:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void doStart() {
        isStart =true;
        Log.v("chad",isStart+"");
        mt=new MyClockTask();
        timer.schedule(mt,0,1000);
    }
    private void doStop() {
        isStart =false;
        Log.v("chad",isStart+"");
        mt.cancel();
    }
    private void doReset(){
        i =0;
    }
    private  class MyClockTask extends TimerTask {
        @Override
        public void run() {
//            Log.v("chad",i+"");
            Intent it = new Intent("now");
            it.putExtra("now",i);
            TimerService.this.sendBroadcast(it);
            i++;

        }
    }
}
