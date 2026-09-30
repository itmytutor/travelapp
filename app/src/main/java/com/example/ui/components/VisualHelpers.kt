package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BookedPurple
import com.example.ui.theme.BookedPurpleBg
import com.example.ui.theme.DreamingAmber
import com.example.ui.theme.DreamingAmberBg
import com.example.ui.theme.PlanningBlue
import com.example.ui.theme.PlanningBlueBg
import com.example.ui.theme.SunsetCoralSecondary
import com.example.ui.theme.VisitedGreen
import com.example.ui.theme.VisitedGreenBg
import java.text.NumberFormat
import java.util.Locale

object VisualHelpers {

    fun getCategoryIcon(category: String): ImageVector {
        return when (category.lowercase()) {
            "beach" -> Icons.Default.BeachAccess
            "mountain" -> Icons.Default.Landscape
            "culture" -> Icons.Default.Museum
            "city" -> Icons.Default.LocationCity
            "safari" -> Icons.Default.Pets
            "wonder" -> Icons.Default.Star
            "food & wine", "food" -> Icons.Default.Restaurant
            else -> Icons.Default.Place
        }
    }

    fun getPresetGradient(preset: String): Brush {
        return when (preset.lowercase()) {
            "beach" -> Brush.verticalGradient(
                listOf(Color(0xFF0284C7), Color(0xFF06B6D4), Color(0xFFFDE047))
            )
            "mountain" -> Brush.verticalGradient(
                listOf(Color(0xFF1E293B), Color(0xFF475569), Color(0xFF94A3B8))
            )
            "culture" -> Brush.verticalGradient(
                listOf(Color(0xFF831843), Color(0xFFBE185D), Color(0xFFF43F5E))
            )
            "city" -> Brush.verticalGradient(
                listOf(Color(0xFF312E81), Color(0xFF4338CA), Color(0xFF818CF8))
            )
            "safari" -> Brush.verticalGradient(
                listOf(Color(0xFF78350F), Color(0xFFB45309), Color(0xFFF59E0B))
            )
            "aurora" -> Brush.verticalGradient(
                listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981))
            )
            "island" -> Brush.verticalGradient(
                listOf(Color(0xFF0E7490), Color(0xFF0891B2), Color(0xFF22D3EE))
            )
            else -> Brush.verticalGradient(
                listOf(Color(0xFF0284C7), Color(0xFF0D9488))
            )
        }
    }

    fun formatCurrency(amount: Double, currency: String = ""): String {
        return try {
            "$${String.format(Locale.US, "%,.0f", amount)}"
        } catch (e: Exception) {
            "$$amount"
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status.lowercase()) {
        "visited" -> Triple(VisitedGreenBg, VisitedGreen, Icons.Default.CheckCircle)
        "booked" -> Triple(BookedPurpleBg, BookedPurple, Icons.Default.FlightTakeoff)
        "planning" -> Triple(PlanningBlueBg, PlanningBlue, Icons.Default.Flight)
        else -> Triple(DreamingAmberBg, DreamingAmber, Icons.Default.WbSunny)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PriorityBadge(
    priority: String,
    modifier: Modifier = Modifier
) {
    val (dotColor, label) = when (priority.lowercase()) {
        "high" -> Pair(Color(0xFFEF4444), "High Priority")
        "medium" -> Pair(SunsetCoralSecondary, "Medium")
        else -> Pair(Color(0xFF64748B), "Someday")
    }

    Row(
        modifier = modifier
            .background(dotColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = dotColor
        )
    }
}

@Composable
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = VisualHelpers.getCategoryIcon(category),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = category,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
