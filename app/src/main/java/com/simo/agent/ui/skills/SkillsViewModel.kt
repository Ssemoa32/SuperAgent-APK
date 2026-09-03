package com.simo.agent.ui.skills

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SkillsUiState(
    val skills: List<Skill> = defaultSkills,
    val selectedCategory: SkillCategory? = null,
    val searchQuery: String = ""
)

class SkillsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SkillsUiState())
    val uiState: StateFlow<SkillsUiState> = _uiState.asStateFlow()

    fun toggleSkill(skillId: String) {
        val updated = _uiState.value.skills.map { skill ->
            if (skill.id == skillId) skill.copy(isEnabled = !skill.isEnabled)
            else skill
        }
        _uiState.value = _uiState.value.copy(skills = updated)
    }

    fun filterByCategory(category: SkillCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun getFilteredSkills(): List<Skill> {
        return _uiState.value.skills.filter { skill ->
            val matchesCategory = _uiState.value.selectedCategory == null ||
                    skill.category == _uiState.value.selectedCategory
            val matchesSearch = _uiState.value.searchQuery.isEmpty() ||
                    skill.name.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                    skill.nameAr.contains(_uiState.value.searchQuery)
            matchesCategory && matchesSearch
        }
    }
}
