/*
 * Copyright (c) Vasyutchenko Alexey  2024. Last modified 16.01.2024, 21:01
 * ss.plexus@gmail.com
 */

package ru.ssnexus.taganrogwater.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.ssnexus.taganrogwater.App

class GetDataWorker(context: Context, workerParameters: WorkerParameters):
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        App.instance.interactor.getData()
        return Result.success()
    }
}