package com.learn.worlds.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface LearningItemDao {
    @Query("SELECT * FROM learningItems ORDER BY id DESC")
    fun getLearningItems(): Flow<List<LearningItemDB>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningItem(item: LearningItemDB)

    @Query("SELECT * from learningItems WHERE id = :id")
    fun getLearningItem(id: Int): Flow<LearningItemDB>
}
enum class LearningStatusDB{
    LEARNING, KNOWLEDGE
}
@Entity(tableName = "learningItems", indices = [Index(value = ["native_data", "foreign_data"], unique = true)])
data class LearningItemDB(@ColumnInfo(name = "native_data") val nativeData: String,
                          @ColumnInfo(name = "foreign_data")  val foreignData: String,
                          @ColumnInfo(name = "learning_status") val learningStatus: String = LearningStatusDB.LEARNING.name,
                          @ColumnInfo(name = "was_showed_details") val wasTriedToShowDetailsByUser: Boolean = false){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var uid: Int = 0
}
