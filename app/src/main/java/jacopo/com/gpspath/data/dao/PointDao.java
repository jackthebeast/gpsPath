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
    public long add(Point point);

    @Query("select * from point where path = :pathId order by timestamp asc")
    public List<Point> getPointsPerPath(long pathId);

    @Query("delete from point")
    public void clearTable();
}
