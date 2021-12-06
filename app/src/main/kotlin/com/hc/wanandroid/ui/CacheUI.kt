package com.hc.wanandroid.ui

import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.text.NumberFormat
import javax.inject.Inject
import kotlin.io.path.*
import kotlin.streams.toList



@OptIn(ExperimentalMaterialApi::class,ExperimentalAnimationApi::class)
@Composable
fun LocalCacheUI() {
    val vm: LocalCacheViewModel = hiltViewModel()

    LazyColumn(
        contentPadding = PaddingValues(16.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "[${vm.currSize}] ${vm.currPath}",
                Modifier
                    .clickable(!vm.isCachePath) {
                        vm.prev()
                    },
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(vm.pathList, key = { it.name }) {

            val text = buildAnnotatedString {
                append('[')
                withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                    append(if (it.isDirectory) "目录" else "文件")
                }
                append(' ')
                withStyle(SpanStyle(MaterialTheme.colorScheme.primaryContainer)) {
                    append(it.fileSize)
                }
                append("] ")
                append(it.name)
            }

            val swipeableState = rememberSwipeableState(0)

            val offsetX = swipeableState.offset.value.toInt()

            Box(
                Modifier
                    .swipeable(
                        swipeableState,
                        mapOf(0f to 0, -100f to 1),
                        Orientation.Horizontal
                    )
            ) {

                Text(
                    text,
                    Modifier
                        .fillParentMaxWidth()
                        .align(Alignment.CenterStart)
                        .offset {
                            IntOffset(offsetX, 0)
                        }
                        .clickable(enabled = it.isDirectory) {
                            vm.next(it.name)
                        },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (offsetX < -10)
                    Icon(
                        Icons.Default.Delete,
                        null,
                        Modifier
                            .offset {
                                IntOffset(100 + offsetX, 0)
                            }
                            .align(Alignment.CenterEnd)
                            .size(20.dp)
                            .clickable {
                                vm.delete(it)
                            }
                    )

            }


        }
    }

}

data class FileData(
    val path: Path,
    val fileSize: String,
    val name: String,
    val isDirectory: Boolean
)

@HiltViewModel
class LocalCacheViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    companion object {
        private val Long.KB
            get() = this / 1024.0

        private val Long.MB
            get() = this / 1024 / 1024.0

        private val Long.GB
            get() = this / 1024 / 1024 / 1024.0

        private const val mbSize = 1024 * 1024
    }

    private val cachePath = application.cacheDir.parentFile!!.toPath()

    private val externalPath = application.externalCacheDir!!.parentFile!!.toPath()

    var currPath by mutableStateOf(cachePath)

    var isCachePath = true

    var currSize by mutableStateOf("0KB")

    private val nf = NumberFormat.getNumberInstance()
        .also {
            it.maximumFractionDigits = 2
        }

    val pathList = mutableStateListOf<FileData>()
        .also {
            viewModelScope.launch {
                it.addAll(
                    withContext(Dispatchers.IO) { cachePath.toFileData() }
                )
                pathSize(currPath)
            }
        }

    fun next(nextName: String) {
        currPath /= nextName

        updatePath()
    }

    fun prev() {
        currPath = currPath.parent

        updatePath()
    }

    fun delete(fileData: FileData) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!fileData.isDirectory && fileData.path.deleteIfExists()) {
                updatePath()
                return@launch
            }

            val paths = Files.walk(fileData.path).filter {
                val d = it.isDirectory()
                if (!d) it.deleteIfExists()
                d
            }.map { it.toFile() }.toList() as MutableList

            while (paths.isNotEmpty())
                paths.removeIf {
                    if (it.list().isNullOrEmpty())
                        it.delete()
                    else
                        false
                }

            updatePath()
        }
    }

    private fun updatePath() {
        viewModelScope.launch(Dispatchers.IO) {

            pathSize(currPath)
            isCachePath = currPath == cachePath

            val list = currPath.toFileData()

            pathList.clear()
            pathList.addAll(list)
        }

    }

    private fun Path.toFileData(): List<FileData> {
        return Files.list(this)
            .map {
                val isDirectory = it.isDirectory()
                FileData(
                    it, sizeFormat(
                        if (isDirectory)
                            Files.walk(it).mapToLong { p -> p.fileSize() }.sum()
                        else
                            it.fileSize()
                    ), it.name, isDirectory
                )
            }
            .toList()
    }

    private fun pathSize(path: Path) {
        viewModelScope.launch(Dispatchers.IO) {
            currSize = sizeFormat(
                Files.walk(path).mapToLong {
                    it.fileSize()
                }.sum()
            )
        }
    }

    fun sizeFormat(length: Long): String {
        return if (length > mbSize) {
            nf.format(length.MB) + "mb"
        } else {
            nf.format(length.KB) + "kb"
        }
    }


}


