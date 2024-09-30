    package com.josuerdx.lab08.data.database

    import androidx.room.Database
    import androidx.room.Room
    import androidx.room.RoomDatabase
    import androidx.room.migration.Migration
    import androidx.sqlite.db.SupportSQLiteDatabase
    import android.content.Context
    import com.josuerdx.lab08.data.dao.TaskDao
    import com.josuerdx.lab08.data.model.Task

    @Database(entities = [Task::class], version = 2)
    abstract class TaskDatabase : RoomDatabase() {

        abstract fun taskDao(): TaskDao

        companion object {
            @Volatile
            private var INSTANCE: TaskDatabase? = null

            fun getDatabase(context: Context): TaskDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        "task_db"
                    )
                        .addMigrations(MIGRATION_1_2) // Aquí se añade la migración
                        .build()
                    INSTANCE = instance
                    instance
                }
            }

            // Definición de la migración
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // Crear una nueva tabla con todos los campos necesarios
                    database.execSQL("CREATE TABLE new_tasks (id INTEGER PRIMARY KEY NOT NULL, title TEXT, description TEXT)")

                    // Copiar los datos de la tabla anterior
                    // Asegúrate de que todos los campos que quieres copiar estén en la consulta
                    database.execSQL("INSERT INTO new_tasks (id, title, description) SELECT id, title, '' FROM tasks")

                    // Eliminar la tabla anterior
                    database.execSQL("DROP TABLE tasks")

                    // Renombrar la nueva tabla
                    database.execSQL("ALTER TABLE new_tasks RENAME TO tasks")
                }
            }
        }
    }
