package ktUtils

open class GodBotException(message: String? = null) : Exception(message)


class QueueEmptyException: GodBotException()


open class InteractionException : GodBotException()

class ButtonException : InteractionException()


open class NotFoundException: GodBotException()

class MessageNotFoundException: NotFoundException()

class ApplicationNotFoundException: NotFoundException()

class JDANotFoundException: NotFoundException()

class GuildNotFoundException: NotFoundException()

class TrackNotFoundException: NotFoundException()

class LoadFailedException: NotFoundException()

class PlaylistNotFoundException: NotFoundException()


open class YouTubeApiException: GodBotException()

class QuotaExpiredException: YouTubeApiException()

class VideoNotFoundException: YouTubeApiException()

class CouldNotExtractVideoInformation: YouTubeApiException()
