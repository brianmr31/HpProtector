package corp.valhalla.HpProtector.webserver;

import android.content.Context;
import android.util.Log;

import fi.iki.elonen.NanoHTTPD;
import corp.valhalla.HpProtector.webserver.handler.CheckRunningController;

public class AndroidWebServer extends NanoHTTPD {
    private Context current;

    public AndroidWebServer(int port, Context current) {
        super(port);
        this.current = current;
    }

    public AndroidWebServer(String hostname, int port, Context current ) {
        super(hostname, port);
        this.current = current;
    }

    @Override
    public Response serve(IHTTPSession session) {
//        String jsonRes = " { results: true } ";
//        Map<String, String> parms = session.getParms();
//        session.getUri();
        Log.i("AndroidWebServer", String.valueOf( session.getUri() ));
        return new CheckRunningController( session , this.current ).init();
    }
}