package com.simo.agent.ui.skills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val DarkBackground = Color(0xFF0D0D0D)
private val CardBg = Color(0xFF161616)
private val NeonCyan = Color(0xFF00F0FF)
private val NeonPurple = Color(0xFF8A2BE2)
private val TextPrimary = Color(0xFFE0E0E0)
private val TextSecondary = Color(0xFFAAAAAA)

@Composable
fun SkillsScreen(
    onSkillClick: (Skill) -> Unit = {},
    viewModel: SkillsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredSkills = viewModel.getFilteredSkills()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "المهارات • Skills",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.search(it) },
            placeholder = { Text("ابحث عن مهارة...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color(0xFF333333),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                CategoryChip(
                    label = "الكل",
                    selected = uiState.selectedCategory == null,
                    onClick = { viewModel.filterByCategory(null) }
                )
            }
            items(SkillCategory.values()) { cat ->
                CategoryChip(
                    label = cat.name,
                    selected = uiState.selectedCategory == cat,
                    onClick = { viewModel.filterByCategory(cat) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skills List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredSkills) { skill ->
                SkillCard(
                    skill = skill,
                    onToggle = { viewModel.toggleSkill(skill.id) },
                    onClick = { onSkillClick(skill) }
                )
            }
        }
    }
}

@Composable
fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
            selectedLabelColor = NeonCyan,
            containerColor = CardBg,
            labelColor = TextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = NeonCyan,
            borderColor = Color(0xFF333333)
        )
    )
}

@Composable
fun SkillCard(skill: Skill, onToggle: () -> Unit, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = skill.icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = skill.nameAr,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = skill.descriptionAr,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            Switch(
                checked = skill.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = NeonCyan,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = Color(0xFF333333)
                )
            )
        }
    }
}
