package ru.ssnexus.taganrogwater.domain

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide.init
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.activity.MainActivity
import ru.ssnexus.taganrogwater.data.MainRepository
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import ru.ssnexus.taganrogwater.preferences.PreferencesProvider
import ru.ssnexus.taganrogwater.utils.addTo
import timber.log.Timber
import java.net.URL

class Interactor(private val repo: MainRepository, private val prefs: PreferencesProvider) {
    private var notificationLiveData =  MutableLiveData <List<NotificationsData>>()
    init {

    }

    fun initDataObservable(main: MainActivity){
            repo.getNotificationsDataObservable().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    Timber.d("ERROR : NotificationsDataObservable Error!")
                }.subscribe{
                if(!it.isEmpty()) {
                    Timber.d("notificationLiveData.postValue(it)")
                    notificationLiveData.postValue(it)
                }
            }.addTo(main.autoDisposable)
    }

    fun getNotificationLiveData() = notificationLiveData

    fun updateMarkedStateById(id: Int){
        repo.updateMarkedStateById(id)
    }

    fun getMarkedStateById(id: Int) = repo.getMarkedStateById(id)

    fun removeNotificationById(id: Int){
        repo.removeNotificationById(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(){
        // Create a new coroutine scope
        val scope = CoroutineScope(Dispatchers.Default)
        // Launch a new coroutine in the scope
        scope.launch {
            val url = URL(AppConstants.DATA_URL)
            val doc: Document = Jsoup.connect(url.toString()).get()
            var element = doc.select("table").get(1)
            val rows = element.select("tr")
            var notifications = ArrayList<String>()
            rows.forEach{row ->
                notifications.add(row.text())
            }
            notifications.removeLast()
            repo.clearData()
            if (!notifications.isEmpty()) repo.putToDb(notifications)
        }
    }
}