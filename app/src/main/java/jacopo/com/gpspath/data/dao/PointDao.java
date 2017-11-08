package jacopo.com.gpspath.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import jacopo.com.gpspath.data.model.Point;

/**
 * Created by jacop on 08/11/2017.
 */

@Dao
public interface PointDao {

    @Insert
    public void add(Point point);

    @Query("select * from point where path = :pathId")
    public List<Point> getPointsPerPath(int pathId);


}
