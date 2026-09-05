# 📚 State of the Art

Digital music listening began with the creation of the .mp3 format and P2P software for transferring files between users. Due to the major issues associated with sharing files without permission, and taking advantage of the growth of Internet connections, the first legal music download application was created: the iTunes Music Store (2003). It reached agreements with several record labels and eventually offered songs from around 200,000 different artists.

Following its success, it became clear that downloading files was not necessary, since music could simply be streamed online. This led to the emergence of Spotify (2008) and Apple Music (2015). The latter, developed by Apple, did not replace the iTunes Music Store, but it became the company’s main music streaming application.

Over time, other platforms have emerged, such as YouTube Music, Google Play Music and Amazon Prime Music, among others. However, this project will focus on analysing the features and design of the two main platforms, Spotify and Apple Music, using them as references for the development of Music Fever.

## 🧩 Features

The core functionality of both Spotify and Apple Music is the ability to search for artists, albums and songs in order to listen to them, save them as favourites or add them to the user’s library. Both platforms also allow users to create playlists, enabling them to organise a selection of songs into personal collections.

The applications collect information about the music users listen to and use it to recommend similar content, either by adding songs to the playback queue or by generating personalised playlists and collections. In addition, these listening habits are used to provide users with monthly statistics about their activity during a given period. More extensive statistics are also provided annually, summarising the user’s listening activity throughout the year — the well-known Spotify Wrapped. Spotify also allows users to view weekly listening statistics.

Spotify also allows users to search for and follow other users, such as friends or other listeners, whereas Apple Music allows users to favourite artists in order to receive notifications whenever new content is added to an artist’s page. Since Spotify includes user profiles, it also provides social features such as chatting with other users, discovering playlists published by other users and saving them to the user’s own library. Following this collaborative approach, Spotify allows users to create collaborative playlists and Jams — collaborative playback queues in which participants can view the current queue and, depending on the host’s settings, control the music being played.

Spotify also offers several additional features, including DJ, playlist blending with friends, a reels-style content system, and even a version of the classic Snake game based on the user’s own playlists, among many other features available on the platform.

| Main feature                     | Spotify | Apple Music |
| -------------------------------- | :-----: | :---------: |
| Music search and playback        |    ✓    |      ✓      |
| Personal music library           |    ✓    |      ✓      |
| Playlist creation                |    ✓    |      ✓      |
| Music recommendations            |    ✓    |      ✓      |
| Listening statistics             |    ✓    |      ✓      |
| User profiles                    |    ✓    |      ✓      |
| User search and following        |    ✓    |      —      |
| Collaborative playlists          |    ✓    |      ✓      |
| Real-time collaborative sessions |    ✓    |      —      |
| Social interaction               |    ✓    |      —      |

## 🎨 User Interface
### 🟢 Spotify
![Spotify IU](/src/spotify_iu.png)

Spotify uses a denser and more information-heavy interface, with several navigation areas visible at the same time. A horizontal header at the top contains the search bar, account-related options, and other global controls. On the left side, a vertical navigation panel provides quick access to the user’s library, playlists, and frequently used sections.

The central area is reserved for the current page content. In the home view, this space is mainly used for recommendations, but it changes dynamically depending on the section being accessed.

The music player remains fixed at the bottom of the interface, making playback controls continuously available regardless of the current page. Overall, Spotify presents a higher visual density, with many options, shortcuts, and content elements displayed simultaneously.

### 🍎 Apple Music
![Apple Music IU](/src/apple_music_iu.png)

Apple Music uses a cleaner and more spacious interface, with fewer elements competing for attention at the same time. A vertical navigation panel on the left provides quick access to the main sections of the application, such as Search, Home, New, and Radio.

The central area is dedicated to the content of the currently selected page and can be vertically scrolled. Compared with Spotify, the layout makes greater use of empty space and presents fewer controls and shortcuts simultaneously, resulting in a lower visual density.

The music player is positioned at the bottom of the interface, remaining accessible while the user navigates through the different sections.