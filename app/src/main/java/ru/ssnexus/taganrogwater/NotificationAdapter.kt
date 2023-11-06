package ru.ssnexus.taganrogwater

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ssnexus.taganrogwater.DetailsActivity
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import ru.ssnexus.taganrogwater.databinding.WaterInfoViewBinding

class NotificationAdapter(private val context: Context, private var notificationsData: ArrayList<NotificationsData>):RecyclerView.Adapter<NotificationAdapter.NotificationHolder>() {
    class NotificationHolder(binding: WaterInfoViewBinding):RecyclerView.ViewHolder(binding.root) {
        val itemContainer = binding.itemContainer
        val date = binding.dateData
        val notification = binding.notificationData
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.NotificationHolder {
        return NotificationHolder(WaterInfoViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: NotificationAdapter.NotificationHolder, position: Int) {
//        val formatter = SimpleDateFormat("dd.MM.yyyy")
//        val dateFromNotif = formatter.parse(srcText)
//        val dateFromNotifStr = formatter.format(dateFromNotif)

        holder.date.text = notificationsData[position].date
        holder.notification.text = notificationsData[position].notifiction

        holder.itemContainer.setOnClickListener {
            DetailsActivity.notificationDate = holder.date.text as String
            DetailsActivity.notificationBody = holder.notification.text as String
            val activity = holder.itemView.context as Activity
            activity.startActivity(Intent(activity, DetailsActivity::class.java))
        }

    }

    override fun getItemCount(): Int {
        return notificationsData.size
    }

    fun updateNotificationsList(_notificationList: List<NotificationsData>){
        notificationsData = ArrayList()
        notificationsData.addAll(_notificationList)

        notifyDataSetChanged()
    }
}