package co.edu.eci.ieti.android.repository;

import android.content.Context;
import java.util.List;
import co.edu.eci.ieti.android.network.model.Task;
import co.edu.eci.ieti.android.storage.dao.TaskDAO;
import co.edu.eci.ieti.android.storage.DataBase;

public class TaskRepository {
    private TaskDAO taskDao;
    private List<Task> allTasks;

    // https://github.com/googlesamples
    public TaskRepository(final Context context) {
        DataBase db = DataBase.getDatabase(context);
        taskDao = db.taskDao();
        allTasks = taskDao.getTasksByPriority();
    }

    public List<Task> getAllTasks() {
        return allTasks;
    }

    public void insert(Task t) {
        DataBase.databaseWriteExecutor.execute(() -> {
            taskDao.insert(t);
        });
    }
}
