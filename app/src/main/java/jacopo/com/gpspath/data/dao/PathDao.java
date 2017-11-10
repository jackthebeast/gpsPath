package jacopo.com.gpspath.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import jacopo.com.gpspath.data.model.Path;

/**
 * Created by jacop on 08/11/2017.
 */

@Dao
public interface PathDao {

    @Insert
    public long add(Path path);

    @Query("select * from path")
    public List<Path> getAll();

    @Query("select * from path where id = :id")
    public Path get(int id);

    @Query("delete from path")
    public void clearTable();

    @Update
    public void update(Path path);

    @Query("select * from path where isOpen = :isOpen")
    public Path getOpenedPath(boolean isOpen);
}
