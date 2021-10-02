package corp.valhalla.HpProtector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import corp.valhalla.HpProtector.services.BatteryStatus;
import corp.valhalla.HpProtector.services.ProtectorService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ClipboardManager myClipboard;
    private ClipData myClip;

    private Button button;

    private ToggleButton toggleButton;
    private TextView textView;
    private BatteryStatus batteryStatus;

    private boolean isServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryStatus = new BatteryStatus( getApplicationContext() );

        textView = (TextView) findViewById(R.id.idAddress);

        toggleButton = (ToggleButton) findViewById(R.id.id);
        toggleButton.setOnClickListener( this );
        if( batteryStatus.isRunning() ){
            toggleButton.setChecked( true );
            textView.setText( batteryStatus.getIdAddressAp() );
        }

        button = (Button) findViewById(R.id.idCopy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String text;
                text = textView.getText().toString();

                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Text Copied",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View arg0) {
        if( toggleButton.isChecked() ){
            if( batteryStatus.isCharging() ) {
                if( ! batteryStatus.isRunning() ){
                    String getIPAddress = getIPAddress();
                    if( getIPAddress != null ){
                        startService(  new Intent( this, ProtectorService.class ) );
                        Toast.makeText(getApplicationContext(),"On", Toast.LENGTH_SHORT).show();
                        batteryStatus.setIdAddressAp( getIPAddress );
                        textView.setText( getIPAddress );
                    }else{
                        toggleButton.setChecked(false);
                        Toast.makeText(getApplicationContext(),"Please Turn On Hotspot", Toast.LENGTH_SHORT).show();
                        textView.setText( "Please Turn On Hotspot" );
                    }
                }else{
                    toggleButton.setChecked( true );
                    Toast.makeText(getApplicationContext(),"Service Already Running", Toast.LENGTH_SHORT).show();
                    textView.setText( batteryStatus.getIdAddressAp() );
                }
            }else{
                toggleButton.setChecked(false);
                Toast.makeText(getApplicationContext(),"Please Pluggin Charger", Toast.LENGTH_SHORT).show();
            }
        }else{
            stopService(new Intent( this, ProtectorService.class ) );
            Toast.makeText(getApplicationContext(),"Off", Toast.LENGTH_SHORT).show();
            textView.setText( "Please Turn On Hotspot" );
        }
    }

    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if( intf.getDisplayName().equals("dummy0") ||
                    intf.getDisplayName().equals("ap0") ||
                    intf.getDisplayName().equals("swlan0") ){
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            if( addr.getHostAddress().contains(".") ){
                                return "http://"+addr.getHostAddress()+":8080/";
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}