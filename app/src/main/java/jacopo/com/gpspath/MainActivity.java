package jacopo.com.gpspath;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

import jacopo.com.gpspath.data.DummyPathProvider;
import jacopo.com.gpspath.data.MapDatabase;
import jacopo.com.gpspath.data.model.Path;
import jacopo.com.gpspath.data.model.Point;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private MapDatabase database;
    private RecyclerView pathsList;
    private PathAdapter pathAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeMap();

        database = MapDatabase.getDatabase(getApplicationContext());

        DummyPathProvider.generateDummyData(database);

        List<Path> paths = database.pathDao().getAll();

        pathsList = (RecyclerView) findViewById(R.id.path_list);

        pathAdapter = new PathAdapter(paths);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        pathsList.setLayoutManager(mLayoutManager);
        pathsList.setItemAnimator(new DefaultItemAnimator());
        pathsList.addItemDecoration(new ListDivider(this));
        pathsList.setAdapter(pathAdapter);


        return;
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
