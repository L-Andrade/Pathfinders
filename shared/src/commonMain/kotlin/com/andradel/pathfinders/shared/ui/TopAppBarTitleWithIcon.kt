package com.andradel.pathfinders.shared.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.go_back
import pathfinders.shared.generated.resources.ic_back

@Composable
fun TopAppBarTitleWithIcon(
    titleRes: StringResource,
    onIconClick: () -> Unit,
    iconRes: DrawableResource = Res.drawable.ic_back,
    iconContentDescription: StringResource? = Res.string.go_back,
    endContent: @Composable RowScope.() -> Unit = {},
) {
    TopAppBarTitleWithIcon(
        title = stringResource(titleRes),
        onIconClick = onIconClick,
        iconRes = iconRes,
        iconContentDescription = iconContentDescription,
        endContent = endContent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarTitleWithIcon(
    title: String,
    onIconClick: () -> Unit,
    iconRes: DrawableResource = Res.drawable.ic_back,
    iconContentDescription: StringResource? = Res.string.go_back,
    endContent: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
        },
        navigationIcon = {
            IconButton(onClick = onIconClick) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = iconContentDescription?.let { stringResource(it) },
                )
            }
        },
        actions = endContent,
    )
}