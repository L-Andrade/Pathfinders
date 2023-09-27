package com.andradel.pathfinders.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andradel.pathfinders.R

@Composable
fun TopAppBarTitleWithIcon(
    @StringRes titleRes: Int,
    onIconClick: () -> Unit,
    @DrawableRes iconRes: Int = R.drawable.ic_back,
    @StringRes iconContentDescription: Int? = R.string.go_back,
    endContent: @Composable RowScope.() -> Unit = {},
) {
    TopAppBarTitleWithIcon(
        title = stringResource(id = titleRes),
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
    @DrawableRes iconRes: Int = R.drawable.ic_back,
    @StringRes iconContentDescription: Int? = R.string.go_back,
    endContent: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
        },
        navigationIcon = {
            IconButton(onClick = onIconClick) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = iconContentDescription?.let { stringResource(id = it) }
                )
            }
        },
        actions = endContent,
    )
}