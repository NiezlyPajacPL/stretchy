package com.example.stretchy.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.features.permissiongranter.GrantPermissions
import com.example.stretchy.navigation.BottomNavigationBar


class MainActivity : ComponentActivity() {
    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = ActivityComponent.create(this)
        setContent {
            var showExportPermissionDialog by remember { mutableStateOf(false) }
            var showImportPermissionDialog by remember { mutableStateOf(false) }
            BottomNavigationBar(
                activityComponent,
                onExportClick = { showExportPermissionDialog = true },
                onImportClick = { showImportPermissionDialog = true }
            )

            /*        Navigation(
                        activityComponent,
                        onExportClick = { showExportPermissionDialog = true },
                        onImportClick = { showImportPermissionDialog = true }
                    )*/
            if (showExportPermissionDialog) {
                ShowPermissionDialogue(
                    permission = WRITE_EXTERNAL_STORAGE,
                    onDismissListener = { showExportPermissionDialog = false })
            } else if (showImportPermissionDialog) {
                ShowPermissionDialogue(
                    permission = READ_EXTERNAL_STORAGE,
                    onDismissListener = { showImportPermissionDialog = false })
            }
        }
    }

    @Composable
    private fun ShowPermissionDialogue(permission: String, onDismissListener: () -> Unit) {
        GrantPermissions(
            permission,
            this,
            this,
            onPermissionRequest = {
                startActivity(it)
            },
            onDismissClick = {
                onDismissListener()
            }
        )
    }
}


