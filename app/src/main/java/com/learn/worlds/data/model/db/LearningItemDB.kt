package com.learn.worlds.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface LearningItemDao {
    @Query("SELECT * FROM learningItems")
    fun getLearningItems(): Flow<@JvmSuppressWildcards List<LearningItemDB>>
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLearningItem(item: LearningItemDB)

    @Query("SELECT * from learningItems WHERE id = :id")
    fun getLearningItem(id: Int): Flow<LearningItemDB>
}

@Entity(tableName = "learningItems")
data class LearningItemDB(@ColumnInfo(name = "native_data") val nativeData: String,
                          @ColumnInfo(name = "foreign_data")  val foreignData: String,
                          @ColumnInfo(name = "learning_status") val learningStatus: String = "LEARNING"){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var uid: Int = 0
}
