package jacopo.com.gpspath.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jacop on 08/11/2017.
 */

@Entity(tableName = "point",
        foreignKeys = {
                @ForeignKey(
                        entity = Path.class,
                        parentColumns = "id",
                        childColumns = "path"
                )},
        indices = { @Index(value = "id")}
)
public class Point {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long path;
    public double lat;
    public double lon;
    public long timestamp;

    public Point(long path, double lat, double lon, long timestamp) {
        this.path = path;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
    }

    public LatLng getLatLng(){
        return new LatLng(lat, lon);
    }
}
