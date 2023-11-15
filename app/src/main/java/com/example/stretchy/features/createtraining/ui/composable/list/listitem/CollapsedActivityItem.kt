package com.example.stretchy.features.createtraining.ui.composable.list.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.stretchy.R
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.composable.widget.toDisplayableLength
import com.example.stretchy.theme.BananaMania

@Composable
fun CollapsedActivityItem(
    exerciseWithBreaks: ExercisesWithBreaks,
    position: Int,
    onExpand: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(72.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onExpand()
            }
            .background(Color(BananaMania.toArgb())),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(64.dp)
                    .background(Gray),
                contentAlignment = Alignment.Center
            ) {
                if (exerciseWithBreaks.nextBreakDuration != 0 && exerciseWithBreaks.nextBreakDuration != null) {
                    Text(toDisplayableLength(exerciseWithBreaks.nextBreakDuration!!))
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_no_break),
                        contentDescription = ""
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${position + 1}")

                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = exerciseWithBreaks.exercise.name,
                        Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}
