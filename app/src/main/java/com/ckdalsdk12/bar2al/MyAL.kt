package com.ckdalsdk12.bar2al

import android.content.Context
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.set
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ckdalsdk12.bar2al.databinding.ActivityMyAlBinding
import com.ckdalsdk12.bar2al.databinding.RecycleItemBinding

// 한글로 된 알레르기 유발 물질 배열
val als = arrayOf("난류", "우유", "메밀", "아황산류", "대두", "잣", "복숭아", "땅콩", "밀", "고등어", "게",
    "새우", "돼지고기", "토마토", "호두", "닭고기", "쇠고기", "오징어", "조개류", "전복", "굴", "홍합")

// 영어로 된 알레르기 유발 물질 배열
val alsEn = arrayOf("egg", "milk", "buckwheat", "sulfurousAcid", "soybean", "pineNut", "peach",
    "peanut", "wheat", "chubMackerel", "crab", "shrimp", "pork", "tomato", "walnut", "chicken",
    "beef", "squid", "shellfish", "abalone", "oyster", "mussel")

// 영어로 된 알레르기 유발 물질 이미지 파일 이름 배열
val alsImgEn = arrayOf("egg", "milk", "buckwheat", "sulfurous_acid", "soybean", "pine_nut", "peach",
    "peanut", "wheat", "chub_mackerel", "crab", "shrimp", "pork", "tomato", "walnut", "chicken",
    "beef", "squid", "shellfish", "abalone", "oyster", "mussel")

// 현재 액티비티 내부에서 체크박스 상태를 저장할 집합
val checkboxStatus = SparseBooleanArray()

class MyAL : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMyAlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 리사이클러뷰 관련 코드
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = MyAdapter(als)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        // SharedPreferences에서 값을 받아와 checkboxStatus 집합에 저장
        val sharedPref = getSharedPreferences("MyAL", Context.MODE_PRIVATE)
        for (i in alsEn.indices) {
            checkboxStatus[i] = sharedPref.getBoolean(alsEn[i], false)
        }
    }

    override fun onStop() {
        super.onStop()
        // SharedPreferences에 checkboxStatus 값 저장
        val binding = ActivityMyAlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = getSharedPreferences("MyAL", Context.MODE_PRIVATE)
        sharedPref.edit().run {
            for (i in alsEn.indices) {
                putBoolean(alsEn[i], checkboxStatus[i])
            }
            commit()
        }
    }
}

// 리사이클러뷰를 위한 코드들

class MyViewHolder(val binding : RecycleItemBinding): RecyclerView.ViewHolder(binding.root)

class MyAdapter(private val als: Array<String>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return als.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(RecycleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        // 리사이클러뷰에 나열된 알레르기마다 각각 체크박스 기록 유지
        binding.ingredientCheckbox.setOnCheckedChangeListener(null)
        binding.ingredientCheckbox.isChecked = checkboxStatus[position]
        binding.ingredientCheckbox.setOnClickListener {
            if (!binding.ingredientCheckbox.isChecked) {
                checkboxStatus.put(position, false)
            }
            else
                checkboxStatus.put(position, true)
            notifyItemChanged(position)
        }
        // 리사이클러뷰에 알레르기 이름과 사진 지정
        binding.ingredientText.text = als[position]
        val context : Context = holder.binding.ingredientImg.context
        val imgId : Int = context.resources.getIdentifier(alsImgEn[position], "drawable", context.packageName)
        binding.ingredientImg.setImageResource(imgId)
    }
}