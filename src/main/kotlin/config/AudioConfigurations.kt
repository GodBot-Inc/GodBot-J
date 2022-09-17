package config

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration
import java.util.concurrent.TimeUnit

val premiumResamplingQuality = AudioConfiguration.ResamplingQuality.HIGH
const val premiumEncoding = AudioConfiguration.OPUS_QUALITY_MAX
val premiumBuffering = TimeUnit.SECONDS.toMillis(5).toInt()

val mediumResamplingQuality = AudioConfiguration.ResamplingQuality.MEDIUM
const val mediumEncoding = 6
val mediumBuffering = TimeUnit.SECONDS.toMillis(3).toInt()

val lowResamplingQuality = AudioConfiguration.ResamplingQuality.LOW
const val lowEncoding = 2
val lowBuffering = TimeUnit.SECONDS.toMillis(1).toInt()
