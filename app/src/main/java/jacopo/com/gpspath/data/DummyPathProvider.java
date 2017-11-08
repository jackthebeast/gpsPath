package jacopo.com.gpspath.data;

import java.util.List;

import jacopo.com.gpspath.data.model.Path;
import jacopo.com.gpspath.data.model.Point;

/**
 * Created by jacop on 08/11/2017.
 */

public abstract class DummyPathProvider {

    public static void generateDummyData(MapDatabase database){
        database.query("delete from point", null);
        database.query("delete from path", null);

        // add some data
        List<Path> paths = database.pathDao().getAll();
        if (paths.size()==0) {
            database.pathDao().add(new Path(2342343, 342335324));
            database.pathDao().add(new Path( 2342343, 342335324));
            paths = database.pathDao().getAll();
            Path path = paths.get(0);

            Point point = new Point(path.id, 43.344, 12.1233, 23124234);
            database.pointDao().add(point);


        }

    }
}
