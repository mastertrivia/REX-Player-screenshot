package xyz.mpv.rex.ui.player.managers

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import `is`.xyz.mpv.MPVLib
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.sync.Mutex
import xyz.mpv.rex.R
import xyz.mpv.rex.preferences.PlayerPreferences

/**
 * Manages video screenshot and snapshot capture, subtitle inclusion configuration,
 * temporary caching, and MediaStore / external storage persistence.
 */
class PlayerSnapshotManager(
  private val playerPreferences: PlayerPreferences,
) {
  private val _isSnapshotLoading = MutableStateFlow(false)
  val isSnapshotLoading: StateFlow<Boolean> = _isSnapshotLoading.asStateFlow()
  private val captureMutex = Mutex()

  fun takeSnapshot(
    context: Context,
    scope: CoroutineScope,
    onImageCaptured: () -> Unit = {},
  ) {
    scope.launch(Dispatchers.IO) {
      // A second tap while MPV is writing would overwrite the same capture pipeline.
      if (!captureMutex.tryLock()) return@launch
      _isSnapshotLoading.value = true
      try {
        val includeSubtitles = playerPreferences.includeSubtitlesInSnapshot.get()

        // Generate filename with timestamp
        val timestamp =
          SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "mpv_snapshot_$timestamp.png"

        // Create a temporary file first
        val tempFile = File(context.cacheDir, filename)

        // Take screenshot using MPV to temp file, with or without subtitles
        if (includeSubtitles) {
          MPVLib.command("screenshot-to-file", tempFile.absolutePath, "subtitles")
        } else {
          MPVLib.command("screenshot-to-file", tempFile.absolutePath, "video")
        }

        // MPV writes asynchronously. Wait for an actual stable, non-empty file instead of
        // imposing an arbitrary delay that is either slow or unreliable on different devices.
        if (!awaitFileReady(tempFile)) {
          withContext(Dispatchers.Main) {
            Toast.makeText(
              context,
              context.getString(R.string.player_sheets_frame_navigation_screenshot_create_failed),
              Toast.LENGTH_SHORT,
            ).show()
          }
          return@launch
        }

        // The flash is a confirmation that the image itself exists, not merely that the
        // control was tapped. Saving to MediaStore continues asynchronously below.
        withContext(Dispatchers.Main) { onImageCaptured() }

        // Use different methods based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          // Android 10+ - Use MediaStore with RELATIVE_PATH
          val contentValues =
            ContentValues().apply {
              put(MediaStore.Images.Media.DISPLAY_NAME, filename)
              put(MediaStore.Images.Media.MIME_TYPE, "image/png")
              put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/mpvSnaps",
              )
              put(MediaStore.Images.Media.IS_PENDING, 1)
            }

          val resolver = context.contentResolver
          val imageUri =
            resolver.insert(
              MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
              contentValues,
            )

          if (imageUri != null) {
            // Copy temp file to MediaStore
            val outputStream =
              resolver.openOutputStream(imageUri)
                ?: throw IllegalStateException(
                  context.getString(R.string.player_sheets_frame_navigation_mediastore_entry_failed),
                )
            outputStream.use { outputStream ->
              tempFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
              }
            }

            // Mark as finished
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, contentValues, null, null)

            // Delete temp file
            tempFile.delete()

            if (playerPreferences.showScreenshotSavedToast.get()) {
              withContext(Dispatchers.Main) {
                Toast
                  .makeText(
                    context,
                    context.getString(R.string.player_sheets_frame_navigation_snapshot_saved),
                    Toast.LENGTH_SHORT,
                  ).show()
              }
            }
          } else {
            throw Exception("Failed to create MediaStore entry")
          }
        } else {
          // Android 9 and below - Use legacy external storage
          @Suppress("DEPRECATION")
          val picturesDir =
            Environment.getExternalStoragePublicDirectory(
              Environment.DIRECTORY_PICTURES,
            )
          val snapshotsDir = File(picturesDir, "mpvSnaps")

          // Create directory if it doesn't exist
          if (!snapshotsDir.exists()) {
            val created = snapshotsDir.mkdirs()
            if (!created && !snapshotsDir.exists()) {
              throw Exception("Failed to create mpvSnaps directory")
            }
          }

          val destFile = File(snapshotsDir, filename)
          tempFile.copyTo(destFile, overwrite = true)
          tempFile.delete()

          // Notify media scanner about the new file
          MediaScannerConnection.scanFile(
            context,
            arrayOf(destFile.absolutePath),
            arrayOf("image/png"),
            null,
          )

          if (playerPreferences.showScreenshotSavedToast.get()) {
            withContext(Dispatchers.Main) {
              Toast
                .makeText(
                  context,
                  context.getString(R.string.player_sheets_frame_navigation_snapshot_saved),
                  Toast.LENGTH_SHORT,
                ).show()
            }
          }
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          Toast.makeText(
            context,
            context.getString(
              R.string.player_sheets_frame_navigation_snapshot_save_failed,
              e.message.orEmpty(),
            ),
            Toast.LENGTH_LONG,
          ).show()
        }
      } finally {
        _isSnapshotLoading.value = false
        captureMutex.unlock()
      }
    }
  }

  private suspend fun awaitFileReady(file: File): Boolean =
    withTimeoutOrNull(FILE_READY_TIMEOUT_MS) {
      var previousSize = -1L
      while (true) {
        val currentSize = if (file.exists()) file.length() else 0L
        if (currentSize > 0L && currentSize == previousSize) break
        previousSize = currentSize
        delay(FILE_READY_POLL_INTERVAL_MS)
      }
      true
    } ?: false

  private companion object {
    const val FILE_READY_TIMEOUT_MS = 5_000L
    const val FILE_READY_POLL_INTERVAL_MS = 20L
  }
}
