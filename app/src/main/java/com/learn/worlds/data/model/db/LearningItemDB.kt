package com.learn.worlds.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface LearningItemDao {
    @Query("SELECT * FROM learningItems")
    fun getLearningItems(): Flow<List<LearningItemDB>>
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLearningItem(item: LearningItemDB)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLearningItems(item: List<LearningItemDB>)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLearningItem(item: LearningItemDB)
    @Query("SELECT * from learningItems WHERE id = :id")
    fun getLearningItem(id: Int): Flow<LearningItemDB>

    // Testing
    @Query("SELECT * FROM learningItems")
    fun getLearningItemsTest(): List<LearningItemDB>
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertLearningItemTest(item: LearningItemDB)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLearningItemsTest(item: List<LearningItemDB>)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLearningItemTest(item: LearningItemDB)
    @Query("SELECT * from learningItems WHERE id = :id")
    fun getLearningItemTest(id: Long): LearningItemDB
}

@Entity(tableName = "learningItems")
data class LearningItemDB(@ColumnInfo(name = "native_data") val nativeData: String,
                          @ColumnInfo(name = "foreign_data")  val foreignData: String,
                          @ColumnInfo(name = "learning_status") val learningStatus: String = "LEARNING",
                          @PrimaryKey @ColumnInfo(name = "id") val timeStampUIID: Long)
