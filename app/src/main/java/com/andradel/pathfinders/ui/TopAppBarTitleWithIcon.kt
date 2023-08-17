package com.andradel.pathfinders.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.andradel.pathfinders.R

@Composable
fun TopAppBarTitleWithIcon(
    @StringRes titleRes: Int,
    onIconClick: () -> Unit,
    @DrawableRes iconRes: Int = R.drawable.ic_back,
    @StringRes iconContentDescription: Int? = R.string.go_back,
    endContent: @Composable RowScope.() -> Unit = {},
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
    TopAppBarTitleWithIcon(
        title = stringResource(id = titleRes),
        onIconClick = onIconClick,
        iconRes = iconRes,
        iconContentDescription = iconContentDescription,
        endContent = endContent,
        elevation = elevation,
    )
}

@Composable
fun TopAppBarTitleWithIcon(
    title: String,
    onIconClick: () -> Unit,
    @DrawableRes iconRes: Int = R.drawable.ic_back,
    @StringRes iconContentDescription: Int? = R.string.go_back,
    endContent: @Composable RowScope.() -> Unit = {},
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
    TopAppBar(
        title = {
            Text(text = title, style = MaterialTheme.typography.subtitle2)
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
        backgroundColor = MaterialTheme.colors.background,
        elevation = elevation
    )
}