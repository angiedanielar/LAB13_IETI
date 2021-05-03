package co.edu.eci.ieti.android.storage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import co.edu.eci.ieti.android.network.converter.DateConverter;
import co.edu.eci.ieti.android.network.model.Task;
import co.edu.eci.ieti.android.network.model.User;
import co.edu.eci.ieti.android.storage.dao.TaskDAO;
import co.edu.eci.ieti.android.storage.dao.UserDAO;

@Database(entities = {Task.class, User.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class DataBase extends RoomDatabase {

    public abstract TaskDAO taskDao();
    public abstract UserDAO userDao();

    private static volatile DataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                TaskDAO dao = INSTANCE.taskDao();
                UserDAO udao = INSTANCE.userDao();
                dao.deleteAll();
                udao.deleteAll();
            });
        }
    };

    public static DataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DataBase.class, "taskplanner_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}