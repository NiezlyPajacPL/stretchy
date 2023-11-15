package com.example.stretchy.features.executetraining.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.theme.BananaMania

@Composable
fun TrainingSummaryComposable(
    numberOfExercises: Int,
    timeSpent: String,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(BananaMania.toArgb()))
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier.padding(top = 48.dp)
        ) {
            Text(
                stringResource(id = R.string.you_finished_training),
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(200.dp))
            Text(
                text = stringResource(id = R.string.total_exercises, numberOfExercises),
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = stringResource(id = R.string.time_spent, timeSpent),
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(260.dp))
            Card(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.navigate(Screen.StretchingListScreen.route) }
                    .height(80.dp)
                    .fillMaxWidth()
                    .padding(12.dp),
                backgroundColor = Color.Black,
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.finish),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}