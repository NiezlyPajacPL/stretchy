package com.example.stretchy.features.createtraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateOrEditTrainingViewModel(val repository: Repository, val trainingId: Long) :
    ViewModel() {
    private val _uiState: MutableStateFlow<CreateTrainingUiState> =
        MutableStateFlow(CreateTrainingUiState.Init)
    val uiState: StateFlow<CreateTrainingUiState> = _uiState

    init {
        if (trainingId != -1L) {
            viewModelScope.launch(Dispatchers.IO) {
                val trainingWithActivities = repository.getTrainingWithActivitiesById(trainingId)
                with(trainingWithActivities) {
                    _uiState.emit(
                        CreateTrainingUiState.Success(
                            trainingId,
                            true,
                            name,
                            activities,
                            false,
                            isCreateTrainingButtonVisible(name, activities),
                        )
                    )
                }
            }
        } else {
            viewModelScope.launch {
                _uiState.emit(
                    CreateTrainingUiState.Success(
                        trainingId,
                        false,
                        "",
                        emptyList(),
                        false,
                        saveButtonCanBeClicked = false
                    )
                )
            }
        }
    }

    fun editTraining(trainingId: Long) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        viewModelScope.launch(Dispatchers.IO) {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                repository.editTrainingWithActivities(
                    trainingId,
                    TrainingWithActivity(
                        stateSuccess.currentName,
                        TrainingType.STRETCH,
                        true,
                        stateSuccess.activities
                    )
                )
                _uiState.emit(CreateTrainingUiState.Done)
            }
        }
    }

    fun removeLocalActivity(exerciseListPosition: Int) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        currentList.removeAt(exerciseListPosition)
        emitActivitiesList(stateSuccess, currentList)
    }

    fun addActivity(activityItem: Activity) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        currentList.add(activityItem)
        emitActivitiesList(stateSuccess, currentList)
    }

    fun editActivity(activityItem: Activity, listId: Int) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        currentList[listId] = activityItem
        emitActivitiesList(stateSuccess, currentList)
    }

    fun setTrainingName(trainingName: String) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        with(stateSuccess) {
            viewModelScope.launch {
                _uiState.value = copy(
                    currentName = trainingName,
                    saveButtonCanBeClicked = isCreateTrainingButtonVisible(
                        trainingName,
                        activities
                    ),
                    isTrainingChanged = isTrainingChanged(
                        trainingId,
                        trainingName,
                        activities
                    )
                )
            }
        }
    }

    fun createTraining() {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        viewModelScope.launch {
            if (stateSuccess.currentName == "") {
                _uiState.emit(CreateTrainingUiState.Error(CreateTrainingUiState.Error.Reason.MissingTrainingName))
            } else if (currentActivitySizeList() < 2) {
                _uiState.emit(CreateTrainingUiState.Error(CreateTrainingUiState.Error.Reason.NotEnoughExercises))
            } else {
                try {
                    saveTraining()
                } catch (ex: Exception) {
                    _uiState.emit(
                        CreateTrainingUiState.Error(
                            CreateTrainingUiState.Error.Reason.Unknown(
                                ex
                            )
                        )
                    )
                }
            }
        }
    }

    fun swapExercises(from: Int, to: Int) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val activities = getCurrentActivities(stateSuccess)
        activities.apply { add(to, removeAt(from)) }
        _uiState.value = stateSuccess.copy(activities = activities)
    }

    private fun currentActivitySizeList(): Int =
        (_uiState.value as? CreateTrainingUiState.Success)?.activities?.size ?: 0

    private fun saveTraining() {
        viewModelScope.launch(Dispatchers.IO) {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                repository.addTrainingWithActivities(
                    TrainingWithActivity(
                        success.currentName,
                        TrainingType.STRETCH,
                        true,
                        success.activities
                    )
                )
                _uiState.emit(CreateTrainingUiState.Done)
            } else {
                //todo toast
            }
        }
    }

    private fun getCurrentActivities(state: CreateTrainingUiState.Success): MutableList<Activity> {
        val currentList = mutableListOf<Activity>()
        currentList.addAll(state.activities)
        return currentList
    }

    private fun isCreateTrainingButtonVisible(
        currentName: String,
        currentExercises: List<Activity>
    ) =
        currentName.isNotBlank() && currentExercises.size >= 2

    private suspend fun isTrainingChanged(
        trainingId: Long?,
        trainingName: String,
        activities: List<Activity>
    ): Boolean {
        var trainingFromDb: TrainingWithActivity? = null
        if (trainingId != null && trainingId >= 0) {
            trainingFromDb = repository.getTrainingWithActivitiesById(trainingId)
            if (trainingFromDb.name == trainingName) {
                if (trainingFromDb.activities.size == activities.size) {
                    trainingFromDb.activities.forEachIndexed { index, activity ->
                        if (activity != activities[index]) {
                            return true
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun emitActivitiesList(
        stateSuccess: CreateTrainingUiState.Success,
        currentList: List<Activity>
    ) {
        with(stateSuccess) {
            viewModelScope.launch {
                _uiState.value = copy(
                    activities = currentList,
                    saveButtonCanBeClicked = isCreateTrainingButtonVisible(
                        currentName,
                        currentList
                    ),
                    isTrainingChanged = isTrainingChanged(
                        trainingId,
                        currentName,
                        currentList
                    )
                )
            }
        }
    }
}