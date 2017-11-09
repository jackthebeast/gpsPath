package jacopo.com.gpspath;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap map;
    private MapDatabase database;
    private RecyclerView pathsList;
    private PathAdapter pathAdapter;
    private List<Path> paths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeMap();

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


        return;
    }


    @Override
    public void onClick(final View view) {
        int itemPosition = pathsList.getChildLayoutPosition(view);
        showPathOnMap(paths.get(itemPosition));
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}
