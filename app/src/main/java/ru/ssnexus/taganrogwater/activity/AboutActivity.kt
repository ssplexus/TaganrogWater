/*
 * Copyright (c) Vasyutchenko Alexey  2023. Last modified 31.12.2023, 12:36
 * ss.plexus@gmail.com
 */

package ru.ssnexus.taganrogwater.activity

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    companion object{
        var author_text = ""
    }

    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TaganrogWater)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.about)

        binding.aboutBody.movementMethod = LinkMovementMethod.getInstance()
        binding.footer.text = author_text
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
}