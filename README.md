# Audible YouTube Mobile

<p float="left">
    <img src="https://github.com/huenique/assets/blob/main/audible-youtube-mobile/library.png?raw=true" width="200" />
    <img src="https://github.com/huenique/assets/blob/main/audible-youtube-mobile/search.png?raw=true" width="200" /> 
    <img src="https://github.com/huenique/assets/blob/main/audible-youtube-mobile/player.png?raw=true" width="200" />
    <img src="https://github.com/huenique/assets/blob/main/audible-youtube-mobile/playlist.png?raw=true" width="200" />
</p>

Audible YouTube Mobile (AYM) is a music player for Android devices.

It allows you to listen to music without advertisements —for free!

It also includes technical benefits:

1. Straightforward user interface (UI).

    AYM is targeted at users who are generally unfamiliar mobile apps. It aims to provide a simple and clean UI that will not confuse the user.

2. Lightweight build.

    AYM is decoupled from its backend capabilities. It relies on backend services for the majority of its features.

3. Mostly I/O bound.

    Since AYM utilizes services to execute functionalities, such as parsing YouTube metadata and converting videos to audio files, it only needs to wait for the completion of input/output operations, like writing to a file or waiting for an HTTP response.
