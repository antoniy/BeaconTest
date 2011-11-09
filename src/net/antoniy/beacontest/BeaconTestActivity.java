package net.antoniy.beacontest;

import java.util.List;

import net.antoniy.beacon.Beacon;
import net.antoniy.beacon.BeaconDeviceEventListener;
import net.antoniy.beacon.BeaconEventListener;
import net.antoniy.beacon.BeaconParams;
import net.antoniy.beacon.DeviceInfo;
import net.antoniy.beacon.exception.BeaconException;
import net.antoniy.beacon.exception.BeaconStoppedException;
import net.antoniy.beacon.exception.InvalidBeaconParamsException;
import net.antoniy.beacon.impl.BeaconFactory;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class BeaconTestActivity extends Activity implements BeaconDeviceEventListener, BeaconEventListener {
	
	private static final String TAG = BeaconTestActivity.class.getSimpleName();
	
	private Beacon beacon;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        BroadcastData data = new BroadcastData();
        data.setDeviceId("af69df6fdf589m");
        data.setTcpHost("192.168.1.1");
        data.setTcpPort("6754");

        BeaconParams beaconParams = BeaconFactory.createBeaconParams();
        beaconParams.setData(data);
        beaconParams.setDataClazz(BroadcastData.class);
        beaconParams.setSendInterval(1000);
        beaconParams.setUdpPort(10010);
        beaconParams.setBeaconTimeout(30000);
        beaconParams.setDataMaxSize(512);
        
        try {
			beacon = BeaconFactory.createBeacon(this, beaconParams);
		} catch (BeaconException e) {
			e.printStackTrace();
		} catch (InvalidBeaconParamsException e) {
			e.printStackTrace();
		}
        
        beacon.addBeaconDeviceEventListener(this);
        beacon.addBeaconEventListener(this);
        
        Button getDiscoveredDevicesButton = (Button) findViewById(R.id.get_discovered_devices);
        getDiscoveredDevicesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				List<DeviceInfo> devices;
				try {
					devices = beacon.getActiveDevices();
				} catch (BeaconStoppedException e) {
					Log.w(TAG, e);
					return;
				}
				
				Log.i(TAG, "List all active devices:");
				for (DeviceInfo deviceInfo : devices) {
					Log.i(TAG, "DeviceInfo: " + deviceInfo.getHash());
				}
			}
        	
        });
    }
    
    @Override
    protected void onPause() {
    	beacon.stopBeacon();
    	
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	try {
    		beacon.startBeacon();
	    } catch (BeaconException e) {
	    	Log.e(TAG, "Can't start beacon." , e);
	    }
    	
    	super.onResume();
    }

	@Override
	public void discoveredBeaconDevice(DeviceInfo deviceInfo) {
		Log.i(TAG, "New device: " + deviceInfo.getHash());
	}

	@Override
	public void updateBeaconDevice(DeviceInfo deviceInfo) {
		Log.i(TAG, "Update device: " + deviceInfo.getHash());
	}

	@Override
	public void removeBeaconDevice(DeviceInfo deviceInfo) {
		Log.i(TAG, "Remove device: " + deviceInfo.getHash());
	}

	@Override
	public void beaconStopped() {
		Log.i(TAG, "Beacon event stopped!");
	}
    
}