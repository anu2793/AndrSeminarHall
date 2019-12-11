package jp.co.hiropro.seminar_hall.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

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
