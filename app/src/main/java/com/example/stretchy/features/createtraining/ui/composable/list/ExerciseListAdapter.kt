package com.example.stretchy.features.createtraining.ui.composable.list

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.stretchy.features.createtraining.ui.composable.list.listitem.ActivityListItem
import com.example.stretchy.features.createtraining.ui.composable.widget.AddExerciseButtonHandler
import java.util.*

class ExerciseListAdapter(
    private var exercisesWithBreaks: List<ExercisesWithBreaks>,
    private val scrollToPosition: (position: Int) -> Unit,
    private val onListChange: (exerciseList: List<ExercisesWithBreaks>) -> Unit,
    private val addExerciseButtonHandler: AddExerciseButtonHandler
) :
    RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val composeView = ComposeView(context)

        return ViewHolder(composeView)
    }

    override fun getItemCount(): Int {
        return exercisesWithBreaks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = exercisesWithBreaks[position]
        holder.composeView.setContent {
            ActivityListItem(
                item,
                position = position,
                onExpand = {
                    getCurrentExpandedItemPosition()?.let {
                        hideExpandedItem(it)
                    }
                    removeBlankItem()
                    expandItem(position)
                },
                onCollapse = {
                    hideExpandedItem(position)
                    onListChange(exercisesWithBreaks)
                },
                isExpanded = item.isExpanded,
                addExerciseButtonHandler = addExerciseButtonHandler
            )
        }
    }

    inner class ViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    fun moveItem(from: Int, to: Int) {
        Collections.swap(exercisesWithBreaks, from, to)
        notifyItemMoved(from, to)
    }

    fun submitList(newList: List<ExercisesWithBreaks>) {
        exercisesWithBreaks = newList
        notifyDataSetChanged()
        if (newList.isNotEmpty()) {
            if (exercisesWithBreaks[exercisesWithBreaks.lastIndex].exercise.name == "") {
                scrollToPosition(exercisesWithBreaks.lastIndex)
            }
            getCurrentExpandedItemPosition()?.let {
                if (exercisesWithBreaks.lastIndex != it) {
                    hideExpandedItem(it)
                }
            }
        }
        onListChange(exercisesWithBreaks)
    }

    fun isItemExpanded(position: Int) = exercisesWithBreaks[position].isExpanded

    fun removeItem(position: Int) {
        val newList = exercisesWithBreaks.toMutableList()
        newList.removeAt(position)
        exercisesWithBreaks = newList
        onListChange(exercisesWithBreaks)
        notifyItemRemoved(position)
    }

    private fun removeBlankItem() {
        if (exercisesWithBreaks[exercisesWithBreaks.lastIndex].exercise.name == "") {
            removeItem(exercisesWithBreaks.lastIndex)
            addExerciseButtonHandler.showButton()
        }
    }

    private fun getCurrentExpandedItemPosition(): Int? {
        val position = exercisesWithBreaks.indexOf(exercisesWithBreaks.find { it.isExpanded })
        return if (position != RecyclerView.NO_POSITION) position else null
    }

    private fun expandItem(position: Int) {
        exercisesWithBreaks[position].isExpanded = true
        notifyItemChanged(position)
    }

    private fun hideExpandedItem(position: Int) {
        exercisesWithBreaks[position].isExpanded = false
        notifyItemChanged(position)
    }
}
