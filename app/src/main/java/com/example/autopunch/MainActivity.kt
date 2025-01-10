package com.example.autopunch

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.util.Calendar

/**
 * 主界面Activity
 * 负责显示打卡时间设置界面和启动后台服务
 */
class MainActivity : AppCompatActivity() {
    private lateinit var timeText: TextView    // 显示当前设置的打卡时间
    private lateinit var setTimeButton: Button // 设置时间的按钮
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        timeText = findViewById(R.id.timeText)
        setTimeButton = findViewById(R.id.setTimeButton)
        
        // 启动前台服务，确保应用在后台持续运行
        startService(Intent(this, PunchService::class.java))
        
        // 设置时间按钮点击事件，显示时间选择器
        setTimeButton.setOnClickListener {
            showTimePickerDialog()
        }
        
        // 启动时加载之前保存的打卡时间
        loadSavedTime()
    }
    
    /**
     * 显示时间选择对话框
     * 用户选择时间后会保存设置并更新显示
     */
    private fun showTimePickerDialog() {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                saveTime(hourOfDay, minute)
                updateTimeDisplay(hourOfDay, minute)
                scheduleWork(hourOfDay, minute)
            },
            0, 0, true
        ).show()
    }
    
    /**
     * 从SharedPreferences加载保存的打卡时间
     * 如果存在保存的时间，则恢复显示并重新调度任务
     */
    private fun loadSavedTime() {
        val prefs = getSharedPreferences("PunchSettings", MODE_PRIVATE)
        val hour = prefs.getInt("hour", -1)
        val minute = prefs.getInt("minute", -1)
        
        if (hour != -1 && minute != -1) {
            updateTimeDisplay(hour, minute)
            scheduleWork(hour, minute)
        }
    }
    
    /**
     * 将打卡时间保存到SharedPreferences
     */
    private fun saveTime(hourOfDay: Int, minute: Int) {
        getSharedPreferences("PunchSettings", MODE_PRIVATE)
            .edit()
            .putInt("hour", hourOfDay)
            .putInt("minute", minute)
            .apply()
    }
    
    /**
     * 更新界面上显示的时间
     */
    private fun updateTimeDisplay(hourOfDay: Int, minute: Int) {
        timeText.text = String.format("%02d:%02d", hourOfDay, minute)
    }
    
    /**
     * 调度定时任务
     * 使用WorkManager创建每天执行一次的周期性任务
     * @param hourOfDay 小时（24小时制）
     * @param minute 分钟
     */
    private fun scheduleWork(hourOfDay: Int, minute: Int) {
        val workManager = WorkManager.getInstance(applicationContext)
        
        // 取消之前的定时任务
        workManager.cancelAllWorkByTag("punch_work")
        
        // 计算到下次执行的延迟时间
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        
        // 如果今天的时间已经过了，就设置为明天这个时间执行
        var delay = calendar.timeInMillis - System.currentTimeMillis()
        if (delay < 0) {
            delay += TimeUnit.DAYS.toMillis(1)
        }
        
        // 创建周期性工作请求，每24小时执行一次
        val workRequest = PeriodicWorkRequestBuilder<PunchWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("punch_work")
            .build()
        
        // 提交工作请求到WorkManager
        workManager.enqueue(workRequest)
    }
} 