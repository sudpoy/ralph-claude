package com.example.photosapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photosapp.presentation.theme.PhotosAppTheme

/**
 * Month section header for the photo grid.
 * Shows month name on left, checkmark and three-dot menu on right.
 */
@Composable
fun MonthHeader(
    monthName: String,
    modifier: Modifier = Modifier,
    onCheckClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Month name on left
        Text(
            text = monthName,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Action icons on right
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkmark icon
            IconButton(
                onClick = onCheckClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Select",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Three-dot menu
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "More options",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthHeaderPreview() {
    PhotosAppTheme {
        MonthHeader(monthName = "January")
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthHeaderLongNamePreview() {
    PhotosAppTheme {
        MonthHeader(monthName = "September 2024")
    }
}
