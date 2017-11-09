package jacopo.com.gpspath.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by jacop on 08/11/2017.
 */

@Entity
public class Path {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long timeStart;
    public long timeEnd;

    public Path( long timeStart, long timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getIdString() {
        return Long.toString(id);
    }


    private String formatTimestamp (long timestamp){
        return new DateTime(timestamp* 1000L).toString("dd/MM/yyyy\nHH:mm:ss");
    }

    public String getStartFormatted() {
        return formatTimestamp(timeStart);
    }

    public String getEndFormatted() {
        return formatTimestamp(timeEnd);
    }

    public static LatLngBounds computeBoundingBox(List<Point> points){
        double s = 90.0;
        double w = 180.0;
        double n = -90.0;
        double e = -180.0;

        for(Point point : points){
            if(point.lat < s)
                s = point.lat;

            if(point.lat > n)
                n = point.lat;

            if(point.lon < w)
                w = point.lon;

            if(point.lon > e)
                e = point.lon;
        }
        LatLng sw = new LatLng(s, w);
        LatLng ne = new LatLng(n, e);

        return new LatLngBounds(sw, ne);
    }
}
