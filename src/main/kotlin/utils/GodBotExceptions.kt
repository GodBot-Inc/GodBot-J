package utils

open class GodBotException(message: String? = null) : Exception(message)


class QueueEmptyException: GodBotException()


open class NotFoundException: GodBotException()

class TrackNotFoundException: NotFoundException()

class LoadFailedException: NotFoundException()

class PlaylistNotFoundException: NotFoundException()


open class YouTubeApiException: GodBotException()

class VideoNotFoundException: YouTubeApiException()

class CouldNotExtractItemInformation: YouTubeApiException()
