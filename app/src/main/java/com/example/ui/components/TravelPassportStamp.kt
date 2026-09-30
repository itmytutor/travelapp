package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Destination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TravelPassportStamp(
    destination: Destination,
    modifier: Modifier = Modifier,
    rotation: Float = -3f
) {
    val stampColor = when ((destination.id % 4).toInt()) {
        0 -> Color(0xFF1E3A8A) // Navy ink
        1 -> Color(0xFF991B1B) // Crimson ink
        2 -> Color(0xFF065F46) // Forest ink
        else -> Color(0xFF86198F) // Purple ink
    }

    val dateStr = destination.visitedDate?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(it)).uppercase()
    } ?: "STAMPED"

    Box(
        modifier = modifier
            .rotate(rotation)
            .border(2.dp, stampColor.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
            .padding(3.dp)
            .border(1.dp, stampColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = stampColor.copy(alpha = 0.9f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PASSPORT ENTRY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = stampColor.copy(alpha = 0.8f),
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val primaryStampText = when {
                destination.cityOrRegion.isNotBlank() -> destination.cityOrRegion
                destination.country.isNotBlank() -> destination.country
                else -> destination.title
            }

            Text(
                text = primaryStampText.uppercase(),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = stampColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            if (destination.country.isNotBlank() && destination.cityOrRegion.isNotBlank()) {
                Text(
                    text = destination.country.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = stampColor.copy(alpha = 0.85f),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = dateStr,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = stampColor.copy(alpha = 0.9f),
                fontFamily = FontFamily.Monospace
            )

            if (destination.rating > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(destination.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = stampColor,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        }
    }
}
