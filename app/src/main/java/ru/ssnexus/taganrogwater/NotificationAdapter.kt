package ru.ssnexus.taganrogwater

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ssnexus.taganrogwater.DetailsActivity
import ru.ssnexus.taganrogwater.databinding.WaterInfoViewBinding

class NotificationAdapter(private val context: Context, private var notificationList: ArrayList<String>):RecyclerView.Adapter<NotificationAdapter.NotificationHolder>() {
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

        val srcText = notificationList[position].trim()
//        val formatter = SimpleDateFormat("dd.MM.yyyy")
//        val dateFromNotif = formatter.parse(srcText)
//        val dateFromNotifStr = formatter.format(dateFromNotif)
        var words = srcText.split(" ") as ArrayList<String>
        if(!words.isEmpty() && words.size > 1){
            holder.date.text = words.removeFirst()
            holder.notification.text = words.joinToString(" ")
        }

        holder.itemContainer.setOnClickListener {
            DetailsActivity.notificationDate = holder.date.text as String
            DetailsActivity.notificationBody = holder.notification.text as String
            val activity = holder.itemView.context as Activity
            activity.startActivity(Intent(activity, DetailsActivity::class.java))
        }

    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    fun updateNotificationsList(_notificationList: ArrayList<String>){
        notificationList = ArrayList()
        notificationList.addAll(_notificationList)

        notifyDataSetChanged()
    }
}