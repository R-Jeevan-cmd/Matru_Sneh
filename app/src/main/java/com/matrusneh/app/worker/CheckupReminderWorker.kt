package com.matrusneh.app.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.matrusneh.app.R

class CheckupReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val checkupName = inputData.getString("checkup_name") ?: "Upcoming Checkup"
        
        showNotification(checkupName)
        
        return Result.success()
    }

    private fun showNotification(name: String) {
        val builder = NotificationCompat.Builder(applicationContext, "matru_sneh_reminders")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use a default icon
            .setContentTitle("Checkup Reminder")
            .setContentText("You have a checkup scheduled: $name")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
