package com.example.flighttrack

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object Utils {
    fun kmToMiles(km: Int): Double {
        return km * 0.621371
    }

    fun loadStopsFromResource(context: Context, resId: Int): List<Stop> {
        val stops = mutableListOf<Stop>()
        val inputStream = context.resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.useLines { lines ->
            lines.forEach {
                val parts = it.split(",")
                if (parts.size == 3) {
                    stops.add(Stop(parts[0], parts[1].toInt(), parts[2]))
                }
            }
        }

        return stops
    }
}
