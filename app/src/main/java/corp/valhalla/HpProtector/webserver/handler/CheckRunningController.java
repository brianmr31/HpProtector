package corp.valhalla.HpProtector.webserver.handler;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

//import corp.valhalla.HpProtector.services.BatteryStatus;
import corp.valhalla.HpProtector.services.BatteryStatus;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class CheckRunningController {

    private Response response;
    private BatteryStatus batteryStatus;

    public CheckRunningController(NanoHTTPD.IHTTPSession session, Context current) {
//        batteryStatus.isRunning();
        this.batteryStatus = new BatteryStatus( current );
        this.response = newFixedLengthResponse( Response.Status.OK, "application/json",  " { \"results\": "+batteryStatus.isCharging()+" } " );
    }

    public Response init() {
        return this.response;
    }
}
