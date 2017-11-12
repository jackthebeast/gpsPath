package jacopo.com.gpspath.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.List;

import jacopo.com.gpspath.data.MapDatabase;

/**
 * Created by jacop on 08/11/2017.
 */

@Entity
public class Path {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long timeStart;
    public long timeEnd;
    public boolean isOpen;

    public Path( long timeStart, long timeEnd, boolean isOpen) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.isOpen = isOpen;
    }

    public String getIdString() {
        return Long.toString(id);
    }


    private String formatTimestamp (long timestamp){
        return new DateTime(timestamp).toString("dd/MM/yyyy\nHH:mm:ss");
    }

    public String getStartFormatted() {
        return formatTimestamp(timeStart);
    }

    public String getEndFormatted() {
        if(timeEnd == 0)
            return "ongoing";
        return formatTimestamp(timeEnd);
    }

    public double getDistance(MapDatabase database){
        List<Point> points = database.pointDao().getPointsPerPath(id);

        if(points==null || points.size()<=1)
            return -1;

        double distance = 0;
        for(int i = 1; i<points.size(); i++){
            Location start = points.get(i-1).getLocation("start");
            Location end =  points.get(i).getLocation("end");

            distance += start.distanceTo(end);
        }

        return distance;
    }

    public String getDuration(){
        Duration duration = null;

        if(timeEnd == 0)
            duration = new Duration(timeStart, System.currentTimeMillis());
        else
            duration = new Duration(timeStart, timeEnd);

        long d = duration.getStandardDays();

        if(d>0)
            if(d == 1)
                return Long.toString(d) + " day";
            else
                return Long.toString(d) + " days";


        d = duration.getStandardHours();

        if(d>0)
            if(d == 1)
                return Long.toString(d) + " hour";
            else
                return Long.toString(d) + " hours";


        d = duration.getStandardMinutes();

        if(d>0)
            if(d == 1)
                return Long.toString(d) + " minute";
            else
                return Long.toString(d) + " minutes";


        d = duration.getStandardSeconds();

        if(d>0)
            if(d == 1)
                return Long.toString(d) + " second";
            else
                return Long.toString(d) + " seconds";


            return "-";
    }

    public static LatLngBounds computeBoundingBox(List<Point> points){
        double s = 90.0;
        double w = 180.0;
        double n = -90.0;
        double e = -180.0;

        if(points.size() == 0)
            return null;

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
