package com.ckdalsdk12.bar2al

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ckdalsdk12.bar2al.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 바코드 스캔 버튼
        binding.btnBarcodeScanner.setOnClickListener {
            val intent = Intent(this, BarcodeScanner::class.java)
            startActivity(intent)
        }

        // 내 알레르기 설정 버튼
        binding.btnMyAllergy.setOnClickListener {
            val intent = Intent(this, MyAL::class.java)
            startActivity(intent)
        }

        // 이전 기록 보기 버튼
        binding.btnLog.setOnClickListener {
            val intent = Intent(this, LogALList::class.java)
            startActivity(intent)
        }

        binding.notice.isSelected = true
    }
}