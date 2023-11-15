package com.example.stretchy.features.permissiongranter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.stretchy.R

@Composable
fun GrantPermissions(
    permission: String,
    applicationContext: Context,
    activity: Activity,
    onPermissionRequest: (intent: Intent) -> Unit,
    onDismissClick: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Dialog(onDismissRequest = { onDismissClick() }) {
            CustomDialogUI(
                onDismissClick = onDismissClick,
                onPermissionRequest = onPermissionRequest,
                applicationContext = applicationContext
            )
        }
    } else {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                permission
            ),
            100
        )
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun CustomDialogUI(
    modifier: Modifier = Modifier,
    onDismissClick: () -> Unit,
    onPermissionRequest: (intent: Intent) -> Unit,
    applicationContext: Context
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier
                .background(Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.permissions_needed),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.permissions_needed_desc),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                        .fillMaxWidth(),
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = {
                    onDismissClick()
                }) {

                    Text(
                        stringResource(id = R.string.decline),
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                TextButton(onClick = {
                    val intent =
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:" + applicationContext.packageName)
                    onPermissionRequest(intent)
                    onDismissClick()
                }) {
                    Text(
                        stringResource(id = R.string.accept),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }

}