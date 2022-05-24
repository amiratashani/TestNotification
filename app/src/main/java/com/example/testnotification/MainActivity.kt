package com.example.testnotification

import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.*
import android.graphics.ImageDecoder.ImageInfo
import android.graphics.PostProcessor
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.TypedValue
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap

class MainActivity : AppCompatActivity() {

    lateinit var btnNotif1: Button
    lateinit var btnNotif2: Button
    lateinit var btnNotifSummary: Button

    val NOTIF1_ID = 100
    val NOTIF2_ID = 200
    val SUMMARY_ID = 0
    val GROUP_KEY_MESSAGE = "com.android.example.MESSAGE"

    val CHANNEL_ID = "CHANNEL.ID.MESSAGE"

    lateinit var newMessageNotification1: Notification
    lateinit var newMessageNotification2: Notification
    lateinit var summaryNotification: Notification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNotif1 = findViewById(R.id.notif1)
        btnNotif2 = findViewById(R.id.notif2)
        btnNotifSummary = findViewById(R.id.notif_summary)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }


        val person1 = Person.Builder()
            .setName("Person 1")
            .setIcon(IconCompat.createWithAdaptiveBitmap(ContextCompat.getDrawable(baseContext,R.drawable.ic_avatar_man_1)
                ?.let { convertAppIconDrawableToBitmap(it) }))
            .build()

        val messagingStyle1: NotificationCompat.MessagingStyle = NotificationCompat.MessagingStyle(person1)
//        messagingStyle1.setConversationTitle()
        messagingStyle1.isGroupConversation = true
        messagingStyle1.addMessage("Message 1",System.currentTimeMillis(),person1)
        messagingStyle1.addMessage("Message 2",System.currentTimeMillis()+10,person1)
//        val message:Notification.MessagingStyle.Message = Notification.MessagingStyle.Message("Message 2",System.currentTimeMillis()+10,person1)
//        message.setData("image/jpeg",)

        newMessageNotification1 = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_white_notif_icon)
            .setContentTitle("ContentTitle Notif 1")
            .setContentText("ContentText Notif 1")
            .setStyle(messagingStyle1)
            .setLargeIcon(ContextCompat.getDrawable(baseContext,R.drawable.ic_avatar_man_1)
                ?.let { convertAppIconDrawableToBitmap(it) })
            .setGroup(GROUP_KEY_MESSAGE)
            .build()


        val person2 = Person.Builder()
            .setName("Person 2")
            .setIcon(IconCompat.createWithAdaptiveBitmap(ContextCompat.getDrawable(baseContext,R.drawable.ic_avatar_man_2)
                ?.let { convertAppIconDrawableToBitmap(it) }))
            .build()

        val messagingStyle2: NotificationCompat.MessagingStyle = NotificationCompat.MessagingStyle(person2)
//        messagingStyle1.setConversationTitle()
        messagingStyle2.setGroupConversation(true)
        messagingStyle2.addMessage("Message 1",System.currentTimeMillis()+20,person2)
        messagingStyle2.addMessage("Message 2",System.currentTimeMillis()+30,person2)


        newMessageNotification2 = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_white_notif_icon)
            .setContentTitle("ContentTitle Notif 2")
            .setContentText("ContentText Notif 2")
            .setLargeIcon(ContextCompat.getDrawable(baseContext,R.drawable.ic_avatar_man_2)
                ?.let { convertAppIconDrawableToBitmap(it) })
            .setStyle(messagingStyle2)
            .setGroup(GROUP_KEY_MESSAGE)
            .build()

        summaryNotification = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setContentTitle("ContentTitle Summary")
            //set content text to support devices running API level < 24
            .setContentText("ContentText Summary")
            .setSmallIcon(R.drawable.ic_stat_white_notif_icon)
            //build summary info into InboxStyle template
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("addLine 1")
                    .addLine("addLine 2")
                    .setBigContentTitle("BigContentTitle InboxStyle")
                    .setSummaryText("SummaryText InboxStyle")
            )
            //specify which group this notification belongs to
            .setGroup(GROUP_KEY_MESSAGE)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

            //set this notification as the summary for the group
            .setGroupSummary(true)
            .build()

        setListener()

    }

    private fun setListener() {
        btnNotif1.setOnClickListener {
            NotificationManagerCompat.from(baseContext).apply {
                notify(NOTIF1_ID, newMessageNotification1)
            }
        }
        btnNotif2.setOnClickListener {
            NotificationManagerCompat.from(baseContext).apply {
                notify(NOTIF2_ID, newMessageNotification2)

            }
        }
        btnNotifSummary.setOnClickListener {
            NotificationManagerCompat.from(baseContext).apply {
                notify(SUMMARY_ID, summaryNotification)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private fun loadRoundAvatar(resId: Int?): IconCompat? {
        if (resId != null) {
            try {
                val bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(resources, resId)
                ) { decoder: ImageDecoder, info: ImageInfo?, src: ImageDecoder.Source? ->
                    decoder.postProcessor = PostProcessor { canvas: Canvas ->
                        val path = Path()
                        path.fillType = Path.FillType.INVERSE_EVEN_ODD
                        val width = canvas.width
                        val height = canvas.height
                        path.addRoundRect(
                            0f,
                            0f,
                            width.toFloat(),
                            height.toFloat(),
                            (width / 2).toFloat(),
                            (width / 2).toFloat(),
                            Path.Direction.CW
                        )
                        val paint = Paint()
                        paint.isAntiAlias = true
                        paint.color = Color.TRANSPARENT
                        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
                        canvas.drawPath(path, paint)
                        PixelFormat.TRANSLUCENT
                    }
                }
                return IconCompat.createWithBitmap(bitmap)
            } catch (ignore: Throwable) {
                return null
            }
        }
        return null
    }

    fun convertAppIconDrawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable)
            return drawable.bitmap
        val appIconSize =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable is AdaptiveIconDrawable)
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    108f,
                    this.resources.displayMetrics
                ).toInt()
            else getAppIconSize(this)
        return drawable.toBitmap(appIconSize, appIconSize, Bitmap.Config.ARGB_8888)
    }

    fun getAppIconSize(context: Context): Int {
        val activityManager = ContextCompat.getSystemService(this, ActivityManager::class.java)!!
        val appIconSize = try {
            activityManager.launcherLargeIconSize
        } catch (e: Exception) {
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                48f,
                context.resources.displayMetrics
            ).toInt()
        }
        return appIconSize
    }
}