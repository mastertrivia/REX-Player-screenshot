package xyz.mpv.rex.preferences

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Segment
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PictureInPictureAlt
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Flip
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.BlurOn
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xyz.mpv.rex.R

/**
 * Represents a customizable button in the player controls.
 * Now includes an icon for the preference UI.
 */
enum class PlayerButton(
  val icon: ImageVector,
) {
  BACK_ARROW(Icons.AutoMirrored.Outlined.ArrowBack),
  VIDEO_TITLE(Icons.Outlined.Title),
  BOOKMARKS_CHAPTERS(Icons.Outlined.Bookmarks),
  PLAYBACK_SPEED(Icons.Outlined.Speed),
  DECODER(Icons.Outlined.Memory),
  SCREEN_ROTATION(Icons.Outlined.ScreenRotation),
  FRAME_NAVIGATION(Icons.Outlined.Camera),
  SCREENSHOT(Icons.Outlined.ContentCut),
  VIDEO_ZOOM(Icons.Outlined.ZoomIn),
  PICTURE_IN_PICTURE(Icons.Outlined.PictureInPictureAlt),
  ASPECT_RATIO(Icons.Outlined.AspectRatio),
  LOCK_CONTROLS(Icons.Outlined.LockOpen),
  AUDIO_TRACK(Icons.Outlined.Audiotrack),
  SUBTITLES(Icons.Outlined.Subtitles),
  MORE_OPTIONS(Icons.Outlined.MoreVert),
  CURRENT_CHAPTER(Icons.Outlined.Bookmarks), // <-- CHANGED ICON
  REPEAT_MODE(Icons.Outlined.Repeat),
  SHUFFLE(Icons.Outlined.Shuffle),
  MIRROR(Icons.Outlined.Flip),
  VERTICAL_FLIP(Icons.Outlined.Flip),
  AB_LOOP(Icons.AutoMirrored.Outlined.Segment),
  CUSTOM_SKIP(Icons.Outlined.FastForward),
  BACKGROUND_PLAYBACK(Icons.Outlined.Headset),
  AMBIENT_MODE(Icons.Outlined.BlurOn),
  SLEEP_TIMER(Icons.Outlined.Timer),
  VIDEO_QUALITY(Icons.Outlined.HighQuality),
  NONE(Icons.Outlined.Bookmarks),
}

/**
 * A list of all buttons that the user can choose from in the customization menu.
 * Excludes NONE (placeholder) and constant buttons (BACK_ARROW, VIDEO_TITLE).
 */
val allPlayerButtons =
  PlayerButton.values().filter {
    it != PlayerButton.NONE &&
      it != PlayerButton.BACK_ARROW &&
      it != PlayerButton.VIDEO_TITLE
  }

/**
 * Gets the human-readable label for a player button.
 */
@Composable
fun getPlayerButtonLabel(button: PlayerButton): String =
  when (button) {
    PlayerButton.BACK_ARROW -> stringResource(R.string.btn_label_back)
    PlayerButton.VIDEO_TITLE -> stringResource(R.string.btn_label_title)
    PlayerButton.BOOKMARKS_CHAPTERS -> stringResource(R.string.btn_label_bookmarks)
    PlayerButton.PLAYBACK_SPEED -> stringResource(R.string.btn_label_speed)
    PlayerButton.DECODER -> stringResource(R.string.btn_label_decoder)
    PlayerButton.SCREEN_ROTATION -> stringResource(R.string.btn_label_rotation)
    PlayerButton.FRAME_NAVIGATION -> stringResource(R.string.btn_label_frame_nav)
    PlayerButton.SCREENSHOT -> stringResource(R.string.btn_label_screenshot)
    PlayerButton.VIDEO_ZOOM -> stringResource(R.string.btn_label_zoom)
    PlayerButton.PICTURE_IN_PICTURE -> stringResource(R.string.btn_label_pip)
    PlayerButton.ASPECT_RATIO -> stringResource(R.string.btn_label_aspect)
    PlayerButton.LOCK_CONTROLS -> stringResource(R.string.btn_label_lock)
    PlayerButton.AUDIO_TRACK -> stringResource(R.string.btn_label_audio)
    PlayerButton.SUBTITLES -> stringResource(R.string.btn_label_subtitles)
    PlayerButton.MORE_OPTIONS -> stringResource(R.string.btn_label_more)
    PlayerButton.CURRENT_CHAPTER -> stringResource(R.string.btn_label_chapter)
    PlayerButton.REPEAT_MODE -> stringResource(R.string.btn_label_repeat_mode)
    PlayerButton.SHUFFLE -> stringResource(R.string.btn_label_shuffle)
    PlayerButton.MIRROR -> stringResource(R.string.btn_label_mirror)
    PlayerButton.VERTICAL_FLIP -> stringResource(R.string.btn_label_vertical_flip)
    PlayerButton.AB_LOOP -> stringResource(R.string.btn_label_ab_loop)
    PlayerButton.CUSTOM_SKIP -> stringResource(R.string.btn_label_custom_skip)
    PlayerButton.BACKGROUND_PLAYBACK -> stringResource(R.string.btn_label_background_playback)
    PlayerButton.AMBIENT_MODE -> stringResource(R.string.btn_label_ambient_mode)
    PlayerButton.SLEEP_TIMER -> stringResource(R.string.btn_label_sleep_timer)
    PlayerButton.VIDEO_QUALITY -> stringResource(R.string.btn_label_quality)
    PlayerButton.NONE -> stringResource(R.string.btn_label_none)
  }

/** Camera-with-focus-corners glyph used by the floating screenshot capture action. */
val ScreenshotCaptureIcon: ImageVector by lazy {
  ImageVector.Builder(
    name = "Screenshot",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
  ).apply {
    path(
      fill = null,
      stroke = SolidColor(Color.Black),
      strokeLineWidth = 1.8f,
    ) {
      moveTo(4f, 8f); lineTo(4f, 4f); lineTo(8f, 4f)
      moveTo(16f, 4f); lineTo(20f, 4f); lineTo(20f, 8f)
      moveTo(20f, 16f); lineTo(20f, 20f); lineTo(16f, 20f)
      moveTo(8f, 20f); lineTo(4f, 20f); lineTo(4f, 16f)
      moveTo(7f, 10f); lineTo(9f, 10f); lineTo(10f, 8.5f); lineTo(14f, 8.5f); lineTo(15f, 10f)
      lineTo(17f, 10f); lineTo(17f, 16f); lineTo(7f, 16f); close()
      moveTo(14f, 13f); arcTo(10f, 11f, 14f, 15f, 0f, 360f, false)
    }
  }.build()
}
