package jacopo.com.gpspath.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import jacopo.com.gpspath.data.dao.PathDao;
import jacopo.com.gpspath.data.dao.PointDao;
import jacopo.com.gpspath.data.model.Path;
import jacopo.com.gpspath.data.model.Point;

/**
 * Created by jacop on 08/11/2017.
 */

@Database(entities = {Path.class,  Point.class
}, version = 16, exportSchema = false)
public abstract class MapDatabase  extends RoomDatabase {
    private static MapDatabase INSTANCE;

    public abstract PathDao pathDao();
    public abstract PointDao pointDao();

    public static MapDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, MapDatabase.class, "database")
                            .allowMainThreadQueries()   //TODO move to background
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
