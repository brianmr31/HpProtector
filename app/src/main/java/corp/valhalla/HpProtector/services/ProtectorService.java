package corp.valhalla.HpProtector.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import corp.valhalla.HpProtector.webserver.AndroidWebServer;

import corp.valhalla.HpProtector.MainActivity;
import corp.valhalla.HpProtector.R;

public class ProtectorService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "ProtectorService";

    private boolean running = true;
    private MediaPlayer player;

    private BatteryStatus batteryStatus;

    private AndroidWebServer androidWebServer;

    private Thread current;
    private final float maxVolume = 100*.01f;
    private AudioManager aManager;

    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Content")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        androidWebServer = new AndroidWebServer( "0.0.0.0", 8080, getApplicationContext() );
        try {
            androidWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player = MediaPlayer.create( this, Settings.System.DEFAULT_RINGTONE_URI );
        player.setLooping( true );
        player.setVolume( maxVolume, maxVolume);
        batteryStatus = new BatteryStatus( getApplicationContext() );

        batteryStatus.setRunning( true );

        aManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        current = new Thread(new Runnable() {
            public void run() {
                while (running) {
                    if (batteryStatus.isCharging()) {
                        if (player.isPlaying()) {
                            player.pause();
//                            player.stop();
                        }
                    } else {
                        if (!player.isPlaying()) {
                            player.start();
                            // Toast.makeText(getApplicationContext(), "TANCAPKAN HP KE CHARGERNYA LAGI!!!", Toast.LENGTH_LONG).show();
                        }
                    }

                    aManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) maxVolume * 20 , 0);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
//                    e.printStackTrace();
                        Log.i(TAG, e.getMessage());
                    }
                }
            }
        });

        current.start();
        // returns the status
        // of the program
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
//        player.stop();
        if( player != null && player.isPlaying() ){
            player.stop();
        }
        androidWebServer.stop();

        batteryStatus.setRunning( false );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
