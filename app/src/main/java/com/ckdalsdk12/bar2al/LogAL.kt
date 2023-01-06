package com.ckdalsdk12.bar2al

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.ckdalsdk12.bar2al.databinding.ActivityLogBinding

class LogAL : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.comparisonMyAL.movementMethod = ScrollingMovementMethod()
        binding.elseAL.movementMethod = ScrollingMovementMethod()

        // 사용자가 선택한 로그의 UID를 나타낼 변수
        var selectedLog = 0

        // 만약 이전 액티비티에서 전달해준 selectedLog라는 이름의 value가 있으면
        // selectedLog 변수에 value 저장
        if (intent.hasExtra("selectedLog")) {
            selectedLog = intent.getIntExtra("selectedLog", 0)
        }

        // DB 접근
        val logDB = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "LogDatabase"
        ).build()

        // DB의 Dao 객체 생성
        val logDatabaseDao = logDB.logDatabaseDao()
        lateinit var log: LogDatabase

        // DB에서 사용자가 선택한 로그의 전체 내용을 받아오는 스레드
        val thread = Thread {
            log = logDatabaseDao.getLogFull(selectedLog)
        }
        thread.start()
        thread.join()

        // 받아온 로그 내용을 출력
        binding.productName.text = log.productName
        binding.comparisonMyAL.text = log.matchedAL
        binding.elseAL.text = log.elseAL
        if (log.productImageURL == "") {
            binding.productImage.visibility = View.GONE
        }
        else {
            Glide.with(this).load(log.productImageURL).into(binding.productImage)
        }
    }
}