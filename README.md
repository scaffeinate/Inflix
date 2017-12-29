# Inflix:
![web_hi_res_512](https://user-images.githubusercontent.com/1825853/31562656-3a7311d2-b02a-11e7-87e6-bdbb5a1ff874.png)

Inflix - Discover movies and TV Shows. Powered by TMDb(themoviedb.org). Developed for Udacity Android Nanodegree Program.

Google Play: https://play.google.com/store/apps/details?id=dev.learn.movies.app.popularmovies_udacity

### Setup: 
- Clone the project. ```git clone https://github.com/scaffeinate/Popular-Movies-Android.git```
- Obtain an api key from [The Movie DB(TMDB)](https://www.themoviedb.org/documentation/api) and add **API_KEY=<YOUR_API_KEY>** to the gradle.properties(Create one if it doesn't exist already) file.

### Screenshots:
![screenshot_1](https://github.com/scaffeinate/Inflix/blob/master/screenshots/phone/phone_collage.jpg?raw=true)
![screenshot_2](https://github.com/scaffeinate/Inflix/blob/master/screenshots/7-tablet/tablet-collage.jpg?raw=true)
![screenshot_3](https://github.com/scaffeinate/Inflix/blob/master/screenshots/10-tablet/10-tablet-collage.jpg?raw=true)

### Changelog:
#### V1.1 [TODO]:
- Search for Movies, TV Shows & Actors separately and show results using a ViewPager.
- Cast Detail Fragment
- SeasonDetails in AdditionInfoActivity
- Reviews Page UI changes.
#### V1.0:
- Added support to discover Movies & TV Shows.
- Brand New UI.
- Added Search functionality.
- Show Additional Details.
- Published to Google Play.

#### V1.0-alpha-2:
- Allow users to view and play trailers ( either in the youtube app or a web browser).
- Allow users to read reviews of a selected movie.
- Allow users to mark a movie as a favorite in the details view by tapping a button(star).
- Create a database and content provider to store the names and ids of the user's favorite movies (and optionally, the rest of the information needed to display their favorites collection while offline).
- Modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.

#### V1.0-alpha-1:
- Present the user with a grid arrangement of movie posters upon launch.
   - Allow your user to change sort order via a setting:
   - The sort order can be by most popular or by highest-rated
- Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
   - original title
   - movie poster image thumbnail
   - A plot synopsis (called overview in the api)
   - user rating (called vote_average in the api)
   - release date

### License:
[**MIT**](https://github.com/scaffeinate/Popular-Movies-Android/blob/master/LICENSE)
