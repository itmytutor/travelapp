package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChecklistItem
import com.example.model.Destination
import com.example.model.PresetStyles
import com.example.model.TravelCategories
import com.example.model.TravelPriorities
import com.example.model.TravelStatuses
import com.example.ui.components.VisualHelpers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDestinationScreen(
    destinationToEdit: Destination?,
    onBackClick: () -> Unit,
    onSave: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)

    val isEditing = destinationToEdit != null

    var title by remember { mutableStateOf(destinationToEdit?.title ?: "") }
    var category by remember { mutableStateOf(destinationToEdit?.category ?: "Culture") }
    var priority by remember { mutableStateOf(destinationToEdit?.priority ?: "High") }
    var status by remember { mutableStateOf(destinationToEdit?.status ?: "Dreaming") }
    var targetDate by remember { mutableStateOf(destinationToEdit?.targetDate ?: "") }
    var estimatedBudgetText by remember {
        mutableStateOf(if ((destinationToEdit?.estimatedBudget ?: 0.0) > 0) destinationToEdit?.estimatedBudget.toString() else "")
    }
    var notes by remember { mutableStateOf(destinationToEdit?.notes ?: "") }
    var selectedPreset by remember { mutableStateOf(destinationToEdit?.imagePreset ?: "beach") }

    val initialChecklist = remember {
        mutableStateListOf<ChecklistItem>().apply {
            if (destinationToEdit != null) {
                addAll(destinationToEdit.checklist)
            }
        }
    }
    var newActivityInput by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Dream Place" else "New Dream Destination") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = true
                                return@Button
                            }

                            val budget = estimatedBudgetText.toDoubleOrNull() ?: 0.0

                            val destination = Destination(
                                id = destinationToEdit?.id ?: 0L,
                                title = title.trim(),
                                country = destinationToEdit?.country ?: "",
                                cityOrRegion = destinationToEdit?.cityOrRegion ?: "",
                                category = category,
                                priority = priority,
                                status = status,
                                targetDate = targetDate.trim(),
                                estimatedBudget = budget,
                                currency = destinationToEdit?.currency ?: "",
                                notes = notes.trim(),
                                activitiesJson = ChecklistItem.serializeList(initialChecklist),
                                rating = destinationToEdit?.rating ?: 0,
                                visitedDate = if (status == "Visited") (destinationToEdit?.visitedDate ?: System.currentTimeMillis()) else null,
                                imagePreset = selectedPreset,
                                createdAt = destinationToEdit?.createdAt ?: System.currentTimeMillis()
                            )
                            onSave(destination)
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("save_destination_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Title
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Destination Title *") },
                    placeholder = { Text("e.g. Kyoto Bamboo Groves & Temples") },
                    isError = titleError,
                    supportingText = { if (titleError) Text("Title is required") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_title"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Category Picker
            item {
                Column {
                    Text(
                        text = "Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TravelCategories.ALL.forEach { cat ->
                            val isSelected = category.equals(cat, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { category = cat },
                                leadingIcon = {
                                    Icon(
                                        imageVector = VisualHelpers.getCategoryIcon(cat),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(cat, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // Priority and Status Pickers
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Priority
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Priority",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TravelPriorities.ALL.forEach { p ->
                                val isSelected = priority.equals(p, ignoreCase = true)
                                Surface(
                                    onClick = { priority = p },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = p,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Status
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Status",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TravelStatuses.ALL.forEach { s ->
                                val isSelected = status.equals(s, ignoreCase = true)
                                Surface(
                                    onClick = { status = s },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = s,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Target Date & Estimated Budget
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { targetDate = it },
                        label = { Text("Target Season / Date") },
                        placeholder = { Text("e.g. Autumn 2026") },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("input_target_date"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = estimatedBudgetText,
                        onValueChange = { estimatedBudgetText = it },
                        label = { Text("Budget") },
                        placeholder = { Text("2500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_budget"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            // Visual Theme / Gradient Preset Selector
            item {
                Column {
                    Text(
                        text = "Visual Banner Theme",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PresetStyles.PRESETS.forEach { (key, name) ->
                            val isSelected = selectedPreset == key
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { selectedPreset = key }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(VisualHelpers.getPresetGradient(key))
                                        .then(
                                            if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Experiences & Activities Checklist Builder
            item {
                Column {
                    Text(
                        text = "Must-Do Experiences & Checklist",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    initialChecklist.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${item.title}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { initialChecklist.removeAt(index) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newActivityInput,
                            onValueChange = { newActivityInput = it },
                            placeholder = { Text("e.g. Sunrise hot air balloon ride...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (newActivityInput.isNotBlank()) {
                                    initialChecklist.add(
                                        ChecklistItem(title = newActivityInput.trim())
                                    )
                                    newActivityInput = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
            }

            // Travel Notes & Insider Tips
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Travel Notes & Insider Tips") },
                    placeholder = { Text("Best seasons, transportation tips, packing reminders, dream restaurants...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("input_notes"),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 4
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
