package jacopo.com.gpspath;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import jacopo.com.gpspath.data.MapDatabase;
import jacopo.com.gpspath.data.model.Path;
import jacopo.com.gpspath.data.model.Point;

/**
 * Created by jacop on 09/11/2017.
 */

public class PositionTrackService extends Service {

    public static final String START_TRACKING_ACTION = "jacopo.com.gpspath.START_TRACKING";
    public static final String STOP_TRACKING_ACTION = "jacopo.com.gpspath.STOP_TRACKING";

    private MapDatabase database;
    private FusedLocationProviderClient locationClient;
    private Path path;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SERVICE", "onStart");

        IntentFilter filter = new IntentFilter();
        filter.addAction(START_TRACKING_ACTION);
        filter.addAction(STOP_TRACKING_ACTION);

        registerReceiver(receiver, filter);

        initDatabase();

        locationClient = new FusedLocationProviderClient(getApplicationContext());

        return Service.START_STICKY;
    }

    private void stopTracking() {
        Log.d("SERVICE", "stopTracking");

        path.isOpen = false;
        database.pathDao().update(path);

        locationClient.removeLocationUpdates(locationCallback);

        //TODO send update UI broadcast
    }

    private void startTracking(){
        if(!checkLocationPermission()) {
            Log.d("SERVICE", "no permission granted");
            return;
        }

        Log.d("SERVICE", "startTracking");
        path = new Path(System.currentTimeMillis(), 0, true);
        path.id = database.pathDao().add(path);

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setFastestInterval(0);
        request.setInterval(5000);
        request.setSmallestDisplacement(25);


        locationClient.requestLocationUpdates(request,locationCallback, null);

        //TODO send update UI broadcast
    }

    private void addPoint(Location location) {
        Log.d("SERVICE", "addPoint");
        Point point = new Point(path.id, location.getLatitude(), location.getLongitude(), System.currentTimeMillis());

        database.pointDao().add(point);

        //TODO send update UI broadcast
    }

    private void initDatabase(){
        Log.d("SERVICE", "initDatabase");
        if(database == null)
            database = MapDatabase.getDatabase(getApplicationContext());
    }

    private boolean checkLocationPermission()
    {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            Log.d("SERVICE", "onLocationResult");
            Location location = result.getLastLocation();

            addPoint(location);
        }
    };



    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("SERVICE", "onReceive: "+intent.getAction());
            String action = intent.getAction();
            if(action.equals(START_TRACKING_ACTION)){
                startTracking();
            }
            else if(action.equals(STOP_TRACKING_ACTION)){
                stopTracking();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(receiver);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PositionTrackService.class);
        context.startService(intent);
    }
}
