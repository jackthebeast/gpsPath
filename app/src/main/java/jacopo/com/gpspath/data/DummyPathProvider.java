package jacopo.com.gpspath.data;

import java.util.List;

import jacopo.com.gpspath.data.model.Path;
import jacopo.com.gpspath.data.model.Point;

/**
 * Created by jacop on 08/11/2017.
 */

public abstract class DummyPathProvider {

    public static void generateDummyData(MapDatabase database){
        database.pointDao().clearTable();
        database.pathDao().clearTable();

        long newId = database.pathDao().add(new Path(1510253649000L, 1510477500000L, false));

        addHomePathPoints(database, newId);

        newId = database.pathDao().add(new Path( 1510313649000L, 1510353649000L, false));

        addColosseoPathPoints(database, newId);


    }

    private static void addHomePathPoints(MapDatabase database, long pathId){
        Point newPoint = new Point(pathId, 41.853387, 12.479164, 1510213649);
        database.pointDao().add(newPoint);
        newPoint = new Point(pathId, 41.854261, 12.479393, 1510223649);
        database.pointDao().add(newPoint);
        newPoint = new Point(pathId, 41.854166, 12.479878, 1510233649);
        database.pointDao().add(newPoint);
        newPoint = new Point(pathId, 41.854328, 12.479980, 1510243649);
        database.pointDao().add(newPoint);
        newPoint = new Point(pathId, 41.854461, 12.480376, 1510253649);
        database.pointDao().add(newPoint);
    }

    private static void addColosseoPathPoints(MapDatabase database, long pathId){
        Point newPoint = new Point(pathId, 41.891455, 12.491596, 1510313649);
        database.pointDao().add(newPoint);
        newPoint = new Point(pathId, 41.890564, 12.491151, 1510323649);
        database.pointDao().add(newPoint);
        newPoint = new Point(pathId, 41.889706, 12.491466, 1510333649);
        database.pointDao().add(newPoint);
        newPoint = new Point(pathId, 41.889451, 12.492785, 1510343649);
        database.pointDao().add(newPoint);
        newPoint = new Point(pathId, 41.889921, 12.493500, 1510353649);
        database.pointDao().add(newPoint);
    }
}
