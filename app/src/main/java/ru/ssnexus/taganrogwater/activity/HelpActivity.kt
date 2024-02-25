/*
 * Copyright (c) Vasyutchenko Alexey  2024. Last modified 23.02.2024, 15:06
 * ss.plexus@gmail.com
 */

package ru.ssnexus.taganrogwater.activity

import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.Display
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.databinding.ActivityHelpBinding
import timber.log.Timber


class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TaganrogWater)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.help)

        binding.helpBody.movementMethod = LinkMovementMethod.getInstance()

        val ssb = SpannableStringBuilder()
        ssb.append("Программа \"Оперативная информация\" осуществляет получение оперативной " +
                "информации с официального сайта ")

        val textWithLink = "МУП «Управление Водоканал» г. Таганрог"
        // Create a SpannableString with the link
        val spannableString = SpannableString(textWithLink)
        // Create a ClickableSpan to handle the click event
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.DATA_URL))
                startActivity(intent)
            }
        }
        spannableString.setSpan(clickableSpan, 0, textWithLink.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.append(spannableString)
        ssb.append(" и оповещает в случае появление новой информации с момена предыдущего опроса. \n\nПолученная информация кэшируется." +
                   "В случае если событие пропадает со страницы оперативной информации, то карточка события в программе " +
                   "помечается как архивная\n"+
                                "\n" +
                "В программе существует два типа карточек уведомлений:\n" +
                "1) карточка актуальной информации\n\n")

        val display: Display = windowManager.defaultDisplay
        val screenWidth: Float = (display.width - display.width * 0.1).toFloat()

        val drawable_help_card: Drawable? = ContextCompat.getDrawable(this, R.drawable.help_card)
        drawable_help_card?.let {
            val dwidth = it.intrinsicWidth.toFloat()
            val dheight = it.intrinsicHeight.toFloat()
            val kdrawable = dwidth / dheight

            it.setBounds(0, 0, screenWidth.toInt(), (screenWidth / kdrawable).toInt() )
            // Create an ImageSpan and set it on the SpannableString
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

            val spannableString_card = SpannableString(" ")
            spannableString_card.setSpan(imageSpan, 0, 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb.append(spannableString_card)
        }

        ssb.append("\n2) карточка архивной информации\n\n")

        val drawable_help_arch: Drawable? = ContextCompat.getDrawable(this, R.drawable.help_arch)
        drawable_help_arch?.let {
            val dwidth = it.intrinsicWidth.toFloat()
            val dheight = it.intrinsicHeight.toFloat()
            val kdrawable = dwidth / dheight

            it.setBounds(0, 0, screenWidth.toInt(), (screenWidth / kdrawable).toInt() )
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

            val spannableString_card = SpannableString(" ")
            spannableString_card.setSpan(imageSpan, 0, 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb.append(spannableString_card)
        }

        ssb.append("\n\nДля карточки актуальной информации доступно действие \"установить напоминание\" (в правом верхнем углу)\n\n")

        val drawable_help_card_notif: Drawable? = ContextCompat.getDrawable(this, R.drawable.help_card_notif)
        drawable_help_card_notif?.let {
            val dwidth = it.intrinsicWidth.toFloat()
            val dheight = it.intrinsicHeight.toFloat()
            val kdrawable = dwidth / dheight

            it.setBounds(0, 0, screenWidth.toInt(), (screenWidth / kdrawable).toInt() )
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

            val spannableString_card = SpannableString(" ")
            spannableString_card.setSpan(imageSpan, 0, 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb.append(spannableString_card)
        }

        ssb.append("\n\nДля архивной карточки доступно действие удалить карточку\n\n")

        val drawable_help_arch_del: Drawable? = ContextCompat.getDrawable(this, R.drawable.help_arch_del)
        drawable_help_arch_del?.let {
            val dwidth = it.intrinsicWidth.toFloat()
            val dheight = it.intrinsicHeight.toFloat()
            val kdrawable = dwidth / dheight

            it.setBounds(0, 0, screenWidth.toInt(), (screenWidth / kdrawable).toInt() )
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

            val spannableString_card = SpannableString(" ")
            spannableString_card.setSpan(imageSpan, 0, 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb.append(spannableString_card)
        }

        ssb.append("\n\nВ настройках программы доступны следующие опции:\n\n" +
                "1) Отображать архив - опция отвечает за показ архивных записей в основной ленте\n\n")

        val drawable_help_set_arch: Drawable? = ContextCompat.getDrawable(this, R.drawable.help_set_arch)
        drawable_help_set_arch?.let {
            val dwidth = it.intrinsicWidth.toFloat()
            val dheight = it.intrinsicHeight.toFloat()
            val kdrawable = dwidth / dheight

            it.setBounds(0, 0, screenWidth.toInt(), (screenWidth / kdrawable).toInt() )
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

            val spannableString_card = SpannableString(" ")
            spannableString_card.setSpan(imageSpan, 0, 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb.append(spannableString_card)
        }

        ssb.append("\n\n2) Показывать уведомления - опция отвечает за показ всех уведомлений (в том числе запланированных)\n\n")

        val drawable_help_set_notif: Drawable? = ContextCompat.getDrawable(this, R.drawable.help_set_notif)
        drawable_help_set_notif?.let {
            val dwidth = it.intrinsicWidth.toFloat()
            val dheight = it.intrinsicHeight.toFloat()
            val kdrawable = dwidth / dheight

            it.setBounds(0, 0, screenWidth.toInt(), (screenWidth / kdrawable).toInt() )
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

            val spannableString_card = SpannableString(" ")
            spannableString_card.setSpan(imageSpan, 0, 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb.append(spannableString_card)
        }

        ssb.append("\n\n3) Актуализировать данные - опция отвечает за автоматическую актуализацию данных (периодический запрос данных с сайта)\n\n")

        val drawable_help_set_upd: Drawable? = ContextCompat.getDrawable(this, R.drawable.help_set_upd)
        drawable_help_set_upd?.let {
            val dwidth = it.intrinsicWidth.toFloat()
            val dheight = it.intrinsicHeight.toFloat()
            val kdrawable = dwidth / dheight

            it.setBounds(0, 0, screenWidth.toInt(), (screenWidth / kdrawable).toInt() )
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

            val spannableString_card = SpannableString(" ")
            spannableString_card.setSpan(imageSpan, 0, 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb.append(spannableString_card)
        }

        ssb.append("\n\n4) Удалить напоминания - кнопка которая убирает все установленные напоминания\n\n")

        val drawable_help_set_delnotif: Drawable? = ContextCompat.getDrawable(this, R.drawable.help_set_delnotif)
        drawable_help_set_delnotif?.let {
            val dwidth = it.intrinsicWidth.toFloat()
            val dheight = it.intrinsicHeight.toFloat()
            val kdrawable = dwidth / dheight

            it.setBounds(0, 0, screenWidth.toInt() / 2, (screenWidth / 2 / kdrawable).toInt() )
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

            val spannableString_card = SpannableString(" ")
            spannableString_card.setSpan(imageSpan, 0, 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb.append(spannableString_card)
        }

        ssb.append("\n\n5) Очистить архив - кнопка которая удаляет архивные карточки\n\n")

        val drawable_help_set_delarch: Drawable? = ContextCompat.getDrawable(this, R.drawable.help_set_delarch)
        drawable_help_set_delarch?.let {
            val dwidth = it.intrinsicWidth.toFloat()
            val dheight = it.intrinsicHeight.toFloat()
            val kdrawable = dwidth / dheight

            it.setBounds(0, 0, screenWidth.toInt() / 2, (screenWidth / 2 / kdrawable).toInt() )
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

            val spannableString_card = SpannableString(" ")
            spannableString_card.setSpan(imageSpan, 0, 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb.append(spannableString_card)
        }

        binding.helpBody.text = ssb
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