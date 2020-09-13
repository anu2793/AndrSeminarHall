package jp.co.hiropro.seminar_hall.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecentlyMessageDao {

    @Insert(onConflict = REPLACE)
    void insertEmploy(RecentlyMessage recentlyMessage);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceEmploy(RecentlyMessage... recentlyMessages);

    @Update(onConflict = REPLACE)
    void updateEmploy(RecentlyMessage recentlyMessage);

    @Query("DELETE FROM RecentlyMessage")
    void deleteAll();

    @Query("SELECT * FROM RecentlyMessage ORDER BY dateTime DESC")
    public List<RecentlyMessage> findAllRecentlyMessageSync();


}
