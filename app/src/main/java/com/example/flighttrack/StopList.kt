package com.example.flighttrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


@Composable
fun StopList(
    stops: List<Stop>,
    useMiles: Boolean,
    currentStopIndex: Int,
    onStopClick: (Int) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(stops) { stop ->
            StopItem(
                stop = stop,
                isCurrent = stops.indexOf(stop) == currentStopIndex,
                useMiles = useMiles,
                onClick = { onStopClick(stops.indexOf(stop)) }
            )
        }
    }

}

@Composable
fun StopItem(
    stop: Stop,
    isCurrent: Boolean,
    useMiles: Boolean,
    onClick: () -> Unit
) {
    val distanceValue = if (useMiles) Utils.kmToMiles(stop.distanceKm) else stop.distanceKm
    val unit = if (useMiles) "miles" else "km"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color.Blue else Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isCurrent) "▶ ${stop.name}" else stop.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCurrent) Color.White else Color.Black
            )
            Text(text = "$distanceValue $unit", fontSize = 16.sp, color = Color.Gray)
            Text(text = "Visa: ${stop.visaRequirement}", fontSize = 16.sp, color = Color.Red)
        }
    }
}
