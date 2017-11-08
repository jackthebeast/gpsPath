package jacopo.com.gpspath.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by jacop on 08/11/2017.
 */

@Entity
public class Path {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public long timeStart;
    public long timeEnd;

    public Path( long timeStart, long timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getIdString() {
        return Integer.toString(id);
    }

    //TODO Joda
    public String getStartFormatted() {
        return Long.toString(timeStart);
    }

    public String getEndFormatted() {
        return Long.toString(timeEnd);
    }
}
