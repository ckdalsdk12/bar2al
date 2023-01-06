package com.ckdalsdk12.bar2al

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.ckdalsdk12.bar2al.databinding.ActivityLogListBinding

class LogALList : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DB 접근
        val logDB = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "LogDatabase"
        ).build()

        // DB의 Dao 객체 생성
        val logDatabaseDao = logDB.logDatabaseDao()
        lateinit var logs: List<String>
        lateinit var logsUID: List<Int>
        lateinit var logsTimeStamp: List<String>

        // DB에서 저장된 로그들의 이름과 UID를 가져오는 스레드
        val thread = Thread {
            logs = logDatabaseDao.getAllLogName()
            logsUID = logDatabaseDao.getAllLogUID()
            logsTimeStamp = logDatabaseDao.getAllLogTimeStamp()
        }
        thread.start()
        thread.join()

        // 받아온 로그들의 이름을 출력하고
        // 사용자가 하나의 로그 선택 시 선택한 로그의 UID를 전달하며 Show_Log 액티비티 실행
        for (i in logs.indices) {
            val id : Int = resources.getIdentifier("log$i", "id", packageName)
            val view : TextView = findViewById(id)
            view.text = logs[i] + "\n" + logsTimeStamp[i]
            view.setOnClickListener {
                val intent = Intent(this, LogAL::class.java)
                intent.putExtra("selectedLog", logsUID[i])
                startActivity(intent)
            }
        }
    }
}