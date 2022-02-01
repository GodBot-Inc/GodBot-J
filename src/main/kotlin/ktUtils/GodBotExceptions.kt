package ktUtils

open class GodBotException : Exception()


class QueueEmptyException: GodBotException()


open class InteractionException : GodBotException()

class ButtonException : InteractionException()


open class NotFoundException: GodBotException()

class CredentialsNotFound: NotFoundException()

class MessageNotFoundException: NotFoundException()

class ApplicationNotFoundException: NotFoundException()

class JDANotFound: NotFoundException()

class PlayerNotFoundException: NotFoundException()

class PlatformNotFoundException: NotFoundException()

class ChannelNotFoundException: NotFoundException()

class GuildNotFoundException: NotFoundException()

class ArgumentNotFoundException: NotFoundException()

class TrackNotFoundException: NotFoundException()

class PlaylistNotFoundException: NotFoundException()


open class CheckFailedException: GodBotException()

class VoiceCheckFailedException: CheckFailedException()


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
