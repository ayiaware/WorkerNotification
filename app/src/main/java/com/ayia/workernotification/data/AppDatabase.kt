package com.ayia.workernotification.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.framework.toEntity
import com.ayia.workernotification.util.DATABASE_NAME
import com.ayia.workernotification.util.GLOBAL_TAG
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {



    abstract fun todoDao(): TodoDao

    companion object {

        // Singleton prevents multiple
        // instances of database opening at the
        // same time.
        // Marks the JVM backing field of the annotated property as volatile,
        // meaning that writes to this field are immediately made visible to other threads.
        val myTag: String = GLOBAL_TAG + " " + AppDatabase::class.java.simpleName

        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            Timber.tag(myTag).d("callback")

                            GlobalScope.launch {
                                instance?.todoDao()?.insertTodo(
                                    listOf(
                                        Todo(
                                            title = "Take trash out",
                                            isDone = true
                                        ),
                                        Todo(
                                            title = "Laundry"
                                        ),
                                        Todo(
                                            title = "Shopping"
                                        ),
                                        Todo(
                                            title = "Cleaning"
                                        ),
                                        Todo(
                                            title = "Laundry"
                                        ),
                                        Todo(
                                            title = "Choir practice"
                                        ),
                                        Todo(
                                            title = "Read"
                                        ),
                                        Todo(
                                            title = "Call mom"
                                        )
                                    ).map {
                                        it.toEntity()
                                    }

                                )
                            }


                        }
                    }
                )
                .fallbackToDestructiveMigration()
                .build()
        }



    }

}

