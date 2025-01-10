package com.example.autopunch

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * 定时任务工作类
 * 负责在指定时间打开钉钉应用
 */
class PunchWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    
    override fun doWork(): Result {
        // 获取钉钉的启动Intent
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage("com.alibaba.android.rimet")  // 钉钉的包名
        
        // 如果找到了钉钉应用，则启动它
        launchIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // 在非Activity上下文中启动Activity需要这个标志
            context.startActivity(it)
        }
        
        return Result.success()
    }
} 