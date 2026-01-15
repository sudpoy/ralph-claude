package com.example.photosapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.photosapp.presentation.components.BottomNavBar
import com.example.photosapp.presentation.components.MemoriesSection
import com.example.photosapp.presentation.components.Memory
import com.example.photosapp.presentation.components.MonthHeader
import com.example.photosapp.presentation.components.NavItem
import com.example.photosapp.presentation.components.PermissionHandler
import com.example.photosapp.presentation.components.TopBar
import com.example.photosapp.presentation.theme.PhotosAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotosAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhotosApp()
                }
            }
        }
    }
}

@Composable
fun PhotosApp() {
    PermissionHandler(
        onPermissionGranted = {
            PhotosContent()
        }
    )
}

@Composable
private fun PhotosContent() {
    var selectedNavItem by remember { mutableStateOf(NavItem.Photos) }

    // Sample memories for display
    val sampleMemories = remember {
        listOf(
            Memory(id = 1, title = "DEC\n2016", imageUri = null, backgroundColor = Color(0xFF6B5B95)),
            Memory(id = 2, title = "JAN\n2017", imageUri = null, backgroundColor = Color(0xFF88B04B)),
            Memory(id = 3, title = "MAR\n2018", imageUri = null, backgroundColor = Color(0xFFF7CAC9)),
            Memory(id = 4, title = "JUL\n2019", imageUri = null, backgroundColor = Color(0xFF92A8D1))
        )
    }

    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Memories/Featured section at the top
            MemoriesSection(
                memories = sampleMemories,
                modifier = Modifier.fillMaxWidth()
            )

            // Month headers with placeholder content
            MonthHeader(monthName = "January")

            // Placeholder for photo grid content
            Text(
                text = "Photos App - ${selectedNavItem.label} Tab",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            MonthHeader(monthName = "December 2024")
        }
    }
}
