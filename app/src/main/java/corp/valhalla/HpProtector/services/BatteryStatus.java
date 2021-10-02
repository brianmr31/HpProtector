package corp.valhalla.HpProtector.services;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryStatus {

    private Context current;

    private int status;
    private boolean bCharging;
    private static String ipAddressAp = "";

    private IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private Intent batteryStatus;

    private static boolean running = false ;

    public BatteryStatus(Context current){
        this.current = current;
    }

    public boolean isRunning(){
        return running;
    }

    public void setRunning( boolean tmp ){
        running = tmp;
    }

    public String getIdAddressAp(){
        return ipAddressAp;
    }

    public void setIdAddressAp( String tmp ){
        ipAddressAp = tmp;
    }

    public boolean isCharging()
    {
        batteryStatus = current.registerReceiver(null, ifilter);
        status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        // Log.i(TAG, String.valueOf(status));
        bCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        // Log.i(TAG, String.valueOf(bCharging));
        return bCharging;
    }
}
