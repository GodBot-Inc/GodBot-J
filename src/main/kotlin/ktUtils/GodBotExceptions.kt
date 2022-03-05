package ktUtils

open class GodBotException(message: String? = null) : Exception(message)


class QueueEmptyException: GodBotException()


open class InteractionException : GodBotException()

class ButtonException : InteractionException()


open class NotFoundException: GodBotException()

class MessageNotFoundException: NotFoundException()

class ApplicationNotFoundException: NotFoundException()

class JDANotFoundException: NotFoundException()

class PlayerNotFoundException: NotFoundException()

class PlatformNotFoundException: NotFoundException()

class ChannelNotFoundException: NotFoundException()

class GuildNotFoundException: NotFoundException()

class ArgumentNotFoundException: NotFoundException()

class TrackNotFoundException: NotFoundException()

class PlaylistNotFoundException: NotFoundException()


open class CheckFailedException(message: String? = null): GodBotException(message)

class VoiceCheckFailedException: CheckFailedException()

class ENVCheckFailedException(message: String? = null): CheckFailedException(message)


open class InvalidException: GodBotException()

class InvalidURLException: InvalidException()


open class RequestException: GodBotException()

class BadRequestException: RequestException()

class EndpointNotFoundException: RequestException()

class EndpointMovedException: RequestException()

class InternalServerException: RequestException()

class RateLimitException: RequestException()


open class YouTubeApiException: RequestException()

class QuotaExpiredException: YouTubeApiException()

class VideoNotFoundException: YouTubeApiException()

class CouldNotExtractVideoInformation: YouTubeApiException()
