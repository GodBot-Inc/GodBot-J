
![Logo](https://i.ds.at/GgNO7w/rs:fill:1200:600/plain/2020/09/25/Gott.jpg)


# GodBot

The GodBot is a Discord Music bot, which delivers High Quality Music with Perfect Discord Integration directly to your Server.


## Authors

- [@RasberryKai](https://github.com/RasberryKai/)

## Environment Variables

To run this project, you will need to add the following environment variables to your .env file

`APPLICATIONID`: Discord Application Id for the bot

`TOKEN`: Discord Bot Token used to start the bot

`DBUSERNAME`: MongoDB Database Username

`DBPASSWORD`: MongoDB Database Password

`YT_API_KEY`: Api Key for the YouTube Data Api V3

`SPOT_CLIENT_ID`: Spotify Api Client Id

`SPOT_CLIENT_SECRET`: Spotify Api Client Secret

## Installation


1. Open the Project in Intellij, and install all Gradle Dependencies
2. Add the .env with the given variables (see Environment Variables)
3. Hit run, and enjoy the music!

## Supported Systems

As of now only these two platforms are supported. The bot doesn't work on
Apple Silicon. If you want to run it on such a system do it on a vm.

- Windows (x86 and x64)
- Linux (x86 and x64, glibc >= 2.15)


## Tech Stack

[**JDA**](https://github.com/DV8FromTheWorld/JDA): Communication with discord. Sending and receiving events.

[**Lavaplayer**](https://github.com/sedmelluq/lavaplayer): Player System, used to manage Songs.

[**YouTube Api**](https://developers.google.com/youtube/v3): Fetch Metadata about YouTube Songs.

[**Spotify Api**](https://developer.spotify.com/documentation/web-api/): Fetch Metadata about Spotify Songs.

[**MongoDB**](https://www.mongodb.com/): Stores information later used by the GodBot.
