package jacopo.com.gpspath;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import jacopo.com.gpspath.data.DummyPathProvider;
import jacopo.com.gpspath.data.MapDatabase;
import jacopo.com.gpspath.data.model.Path;
import jacopo.com.gpspath.data.model.Point;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, CompoundButton.OnCheckedChangeListener{

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

        DummyPathProvider.generateDummyData(database);

        paths = database.pathDao().getAll();

        pathsList = (RecyclerView) findViewById(R.id.path_list);

        pathAdapter = new PathAdapter(paths, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        pathsList.setLayoutManager(layoutManager);
        pathsList.setItemAnimator(new DefaultItemAnimator());
        pathsList.addItemDecoration(new ListDivider(this));
        pathsList.setAdapter(pathAdapter);


        trackingSwitch = (Switch) findViewById(R.id.tracking_switch);
        trackingSwitch.setOnCheckedChangeListener(this);

    }


    @Override
    public void onClick(final View view) {
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

        if(points.size()<2)
            return;

        for(int i = 1; i<points.size(); i++){
            LatLng start = points.get(i-1).getLatLng();
            LatLng end =  points.get(i).getLatLng();
            Polyline line = map.addPolyline(new PolylineOptions()
                    .add(start, end)
                    .width(5)
                    .color(Color.BLUE));
        }

        LatLngBounds bounds = Path.computeBoundingBox(points);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        map.animateCamera(cu);

    }

    protected synchronized void initializeMap() {
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 01: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //TODO disable components
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}
