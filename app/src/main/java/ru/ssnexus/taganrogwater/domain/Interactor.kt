package ru.ssnexus.taganrogwater.domain

import ru.ssnexus.taganrogwater.data.MainRepository
import ru.ssnexus.taganrogwater.preferences.PreferencesProvider

class Interactor(private val repo: MainRepository, private val prefs: PreferencesProvider) {
    init {

    }
}