package com.example.autopunch

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * 前台服务
 * 通过显示持久通知来保持应用在后台运行
 */
class PunchService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 创建通知渠道（Android 8.0及以上必需）
        createNotificationChannel()
        
        // 创建前台服务通知
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("自动打卡服务运行中")
            .setContentText("保持后台运行以确保准时打卡")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
        
        // 启动前台服务
        startForeground(1, notification)
        
        // 如果服务被系统杀死，会尝试重新启动
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    /**
     * 创建通知渠道
     * Android 8.0（API 26）及以上版本必需
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "打卡服务通知",
            NotificationManager.IMPORTANCE_LOW  // 低重要度，不会发出声音
        )
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
    
    companion object {
        private const val CHANNEL_ID = "PunchServiceChannel"
    }
} 