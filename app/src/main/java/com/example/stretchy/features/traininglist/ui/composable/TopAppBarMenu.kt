package com.example.stretchy.features.traininglist.ui.composable

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.stretchy.R
import com.example.stretchy.features.datatransport.DataExporterImpl
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader


@Composable
fun Menu(
    viewModel: TrainingListViewModel,
    onExportPermissionsNeeded: () -> Unit,
    onImportPermissionsNeeded: () -> Unit
) {
    val context = LocalContext.current
    val filePickerIntent = Intent()
        .setType("*/*")
        .setAction(Intent.ACTION_GET_CONTENT)
    var isImportAppend = true
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val fileData = getResultData(result, context)
            if (!fileData.isNullOrBlank()) {
                CoroutineScope(Dispatchers.Default).launch {
                    if (isImportAppend) {
                        viewModel.importByAppending(fileData)
                    } else {
                        viewModel.importByOverriding(fileData)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, R.string.trainings_imported, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            } else {
                Toast.makeText(context, R.string.wrong_format, Toast.LENGTH_LONG)
                    .show()
            }
        }
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    IconButton(
        onClick = {
            menuExpanded = true
        }
    ) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(id = R.string.desc_menu_icon),
            tint = Color.White
        )
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    menuExpanded = false
                    isImportAppend = true
                    if (isPermissionsGranted(context, READ_EXTERNAL_STORAGE)) {
                        filePicker.launch(filePickerIntent)
                    } else {
                        onImportPermissionsNeeded()
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.import_and_append))
            }
            DropdownMenuItem(
                onClick = {
                    menuExpanded = false
                    isImportAppend = false
                    if (isPermissionsGranted(context, READ_EXTERNAL_STORAGE)) {
                        filePicker.launch(filePickerIntent)
                    } else {
                        onImportPermissionsNeeded()
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.import_and_override))
            }
            DropdownMenuItem(
                onClick = {
                    menuExpanded = false
                    if (isPermissionsGranted(context, WRITE_EXTERNAL_STORAGE)) {
                        viewModel.export()
                        Toast.makeText(context, R.string.trainings_exported, Toast.LENGTH_LONG)
                            .show()
                    } else {
                        onExportPermissionsNeeded()
                    }

                }
            ) {
                Text(text = stringResource(id = R.string.export_trainings))
            }
        }
    }
}

private fun getResultData(
    result: androidx.activity.result.ActivityResult,
    context: Context,
): String? {
    val data: Uri? = result.data?.data
    if (data != null &&
        getFileName(data, context).contains(DataExporterImpl.dataTransportFileExt)
    ) {
        val inputStream = context.contentResolver.openInputStream(data)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { line ->
            stringBuilder.append(line)
        }

        return stringBuilder.toString()
    }
    return null
}

private fun getFileName(uri: Uri, context: Context): String {
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
    val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val fileName = if (nameIndex != null && cursor.moveToFirst()) {
        cursor.getString(nameIndex)
    } else {
        uri.lastPathSegment ?: ""
    }
    cursor?.close()
    return fileName
}


private fun isPermissionsGranted(context: Context, permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val result =
            ContextCompat.checkSelfPermission(context, permission)
        result == PackageManager.PERMISSION_GRANTED
    }
}