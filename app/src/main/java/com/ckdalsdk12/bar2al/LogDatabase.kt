package com.ckdalsdk12.bar2al

import androidx.room.*

// 엔티티 정의
@Entity
data class LogDatabase(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "timestamp") val timeStamp: String?,
    @ColumnInfo(name = "product_name") val productName: String?,
    @ColumnInfo(name = "matched_al") val matchedAL: String?,
    @ColumnInfo(name = "else_al") val elseAL: String?,
    @ColumnInfo(name = "productImageURL") val productImageURL: String?
)

// Dao 인터페이스 정의
@Dao
interface LogDatabaseDao {
    // UID값이 I인 로그의 모든 정보를 받아옴
    @Query("SELECT * FROM LogDatabase WHERE uid = :i")
    fun getLogFull(i : Int): LogDatabase

    // 시간 순으로 정렬하여 DB에 저장된 로그들의 제품명을 받아옴
    @Query("SELECT product_name FROM LogDatabase ORDER BY datetime(timestamp) DESC")
    fun getAllLogName(): List<String>

    // 시간 순으로 정렬하여 DB에 저장된 로그들의 UID를 받아옴
    @Query("SELECT uid FROM LogDatabase ORDER BY datetime(timestamp) DESC")
    fun getAllLogUID(): List<Int>

    // 시간 순으로 정렬하여 DB에 저장된 로그들의 UID를 받아옴
    @Query("SELECT timestamp FROM LogDatabase ORDER BY datetime(timestamp) DESC")
    fun getAllLogTimeStamp(): List<String>

    // 만약 DB에 저장된 로그가 5개가 넘었을 경우 가장 오래된 로그를 삭제
    @Query("DELETE FROM LogDatabase WHERE uid NOT IN"+" "+
            "(SELECT uid FROM LogDatabase ORDER BY datetime(timestamp) DESC LIMIT 5)")
    fun deleteOldLog()

    // DB에 인스턴스 입력
    @Insert
    fun insertLog(vararg al: LogDatabase)
}

@Database(entities = [LogDatabase::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDatabaseDao(): LogDatabaseDao
}