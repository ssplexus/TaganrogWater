package ru.ssnexus.taganrogwater.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.databinding.ActivityDetailsBinding
import timber.log.Timber

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    companion object {
        lateinit var notificationDate: String
        lateinit var notificationBody: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TaganrogWater)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.notifDate.text = notificationDate
        binding.notifBody.text = notificationBody

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        Timber.d("DetailsActivity Destroyed")
        super.onDestroy()
    }
}