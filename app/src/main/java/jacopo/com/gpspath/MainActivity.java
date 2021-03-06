package jacopo.com.gpspath;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import jacopo.com.gpspath.data.DummyPathProvider;
import jacopo.com.gpspath.data.MapDatabase;
import jacopo.com.gpspath.data.model.Path;
import jacopo.com.gpspath.data.model.Point;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    public static final String UPDATE_UI = "UPDATE_UI";
    private GoogleMap map;
    private MapDatabase database;
    private RecyclerView pathsList;
    private PathAdapter pathAdapter;
    private List<Path> paths;
    private Switch trackingSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},0);

        initializeMap();

        PositionTrackService.start(getApplicationContext());

        database = MapDatabase.getDatabase(getApplicationContext());

        //DummyPathProvider.generateDummyData(database);

        paths = database.pathDao().getAll();

        pathsList = (RecyclerView) findViewById(R.id.path_list);

        pathAdapter = new PathAdapter(paths, this, database);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        pathsList.setLayoutManager(layoutManager);
        pathsList.setItemAnimator(new DefaultItemAnimator());
        pathsList.addItemDecoration(new ListDivider(this));
        pathsList.setAdapter(pathAdapter);


        trackingSwitch = (Switch) findViewById(R.id.tracking_switch);
        trackingSwitch.setChecked(PositionTrackService.isTracking);
        trackingSwitch.setOnCheckedChangeListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_UI);

        registerReceiver(receiver, filter);

    }

    private void updateUI(){
        paths = database.pathDao().getAll();
        pathAdapter.updateList(paths);
        pathAdapter.notifyDataSetChanged();

        Path openPath = database.pathDao().getOpenedPath(true);
        if(openPath != null) {
            showPathOnMap(openPath);
        }else{
            if (paths != null && paths.size()!=0 && paths.get(0)!=null)
            showPathOnMap(paths.get(0));
        }
    }


    @Override
    public void onClick(final View view) {
        if(trackingSwitch.isChecked()){
            Toast.makeText(this, "Stop tracking to plot other paths", Toast.LENGTH_LONG).show();
            return;
        }
        int itemPosition = pathsList.getChildLayoutPosition(view);
        showPathOnMap(paths.get(itemPosition));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String action = "";
        if(isChecked)
            action = PositionTrackService.START_TRACKING_ACTION;
        else
            action = PositionTrackService.STOP_TRACKING_ACTION;

        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void showPathOnMap(Path path) {
        List<Point> points = database.pointDao().getPointsPerPath(path.id);

        map.clear();

        if(points.size()==1){
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(points.get(0).lat, points.get(0).lon), 15);
            map.animateCamera(cu);
            return;
        }


        for(int i = 1; i<points.size(); i++){
            LatLng start = points.get(i-1).getLatLng();
            LatLng end =  points.get(i).getLatLng();

            PolylineOptions polyOpt = new PolylineOptions();
            polyOpt.add(start, end);
            polyOpt.width(8);

            if(path.isOpen)
                polyOpt.color(Color.RED);
            else
                polyOpt.color(Color.BLUE);

            map.addPolyline(polyOpt);
        }

        LatLngBounds bounds = Path.computeBoundingBox(points);
        if(bounds != null) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
            map.animateCamera(cu);
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ACTIVITY", "onReceive: "+intent.getAction());
            String action = intent.getAction();
            if(action.equals(UPDATE_UI)){
                updateUI();
            }
        }
    };

    protected synchronized void initializeMap() {
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            map.setMyLocationEnabled(true);
        }catch (SecurityException ex){

        }
    }

    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_UI);

        registerReceiver(receiver, filter);
    }
}
