package ru.ssnexus.taganrogwater

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.ssnexus.taganrogwater.activity.DetailsActivity
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import ru.ssnexus.taganrogwater.databinding.WaterInfoViewBinding
import ru.ssnexus.taganrogwater.utils.Utils
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat

class NotificationAdapter(private val context: Context, private var notificationsData: ArrayList<NotificationsData>):RecyclerView.Adapter<NotificationAdapter.NotificationHolder>() {
    class NotificationHolder(binding: WaterInfoViewBinding):RecyclerView.ViewHolder(binding.root) {
        val itemContainer = binding.itemContainer
        val date = binding.dateData
        val notification = binding.notificationData
        val actionBtn = binding.actionBtn
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.NotificationHolder {
        return NotificationHolder(WaterInfoViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: NotificationAdapter.NotificationHolder, position: Int) {
        val id: Int = notificationsData[position].id
        val marked = notificationsData[position].marked
        when (marked){
            -1 -> {
                holder.actionBtn.setImageResource(R.drawable.star_outline_icon)
                holder.itemContainer.setCardBackgroundColor(context.resources.getColor(R.color.water))
            }
             0 -> {
                 holder.actionBtn.setImageResource(R.drawable.delete_icon)
                 holder.itemContainer.setCardBackgroundColor(context.resources.getColor(R.color.gray))
             }
             1 -> {
                 holder.actionBtn.setImageResource(R.drawable.star_rate_icon)
                 holder.itemContainer.setCardBackgroundColor(context.resources.getColor(R.color.water))
             }
        }

        holder.actionBtn.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(context.getString(R.string.period_settings))
                .setMessage(context.getString(
                    when(marked){
                        -1 -> R.string.do_you_want_to_set_mark
                         1 -> R.string.do_you_want_to_remove_mark
                        else -> R.string.do_you_want_to_remove_notification
                    }))
                .setPositiveButton(context.getString(R.string.yes)){ dialog, _ ->
                    runBlocking {
                        val job: Job = launch(context = Dispatchers.Default) {
                            when(marked){
                                 0 -> App.instance.interactor.removeNotificationById(id)
                                 else -> App.instance.interactor.updateMarkedStateById(id)
                            }
                        }
                        job.join()
                    }

                    if(marked < 0 ) {
                        Utils.showSettingsDialog(context)
                        dialog.dismiss()
                    }

                }
                .setNegativeButton(context.getString(R.string.no)){ dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.resources.getColor(R.color.dark_water))
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.dark_water))
        }

        val formatter = SimpleDateFormat("dd.MM.yyyy")
        try{
            val dateFromNotif = formatter.format(notificationsData[position].date)
            holder.date.text = dateFromNotif
        } catch (e: ParseException){
            e.printStackTrace()
        }


        holder.notification.text = notificationsData[position].notifiction

//        holder.itemContainer.setCardBackgroundColor(context.resources.getColor(R.color.gray))
        holder.itemContainer.setOnClickListener {
            DetailsActivity.notificationId = id
            DetailsActivity.notificationMarked = marked
            DetailsActivity.notificationDate = holder.date.text as String
            DetailsActivity.notificationBody = holder.notification.text as String
            val activity = holder.itemView.context as Activity
            activity.startActivity(Intent(activity, DetailsActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return notificationsData.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotificationsList(_notificationList: List<NotificationsData>){
        Timber.d("updateNotificationsList")
        notificationsData = ArrayList()
        notificationsData.addAll(_notificationList)

        if(!App.instance.interactor.getShowArchivePref()){
            val iterator = notificationsData.iterator()
            while (iterator.hasNext()){
                val item = iterator.next()
                if(item.marked == 0) iterator.remove()
            }
        }
        notifyDataSetChanged()
    }
    fun updateRecyclerView(){
        notifyDataSetChanged()
    }
}