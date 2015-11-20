UADN Assignment 2: Popular Movies (Pop-M)
=========================================

TheMovieDB API Key
------------------

For this APP to compile, you need to provide your own themoviedb.org API key.
Place the key in an resource xml file inside the projects res folder.

Ths content of the file should look like this:

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="tmdb_apikey">[YOUR_API_KEY]</string>
</resources>
```

Additional Features Version 1
-----------------------------

- 3 column layout in landscape mode.
- Movies as Parcelables and implementation of onSaveInstanceState.
- The grid view acts as OnSharedPreferenceChangeListener and fetches new movies only at startup or
  preferences change.
  
Additional Features Version 2
-----------------------------

- Movies, trailers and reviews are fetched via CursorLoaders from a ContentProvider which uses
  SQLite for storing data.
- Pull-to-refresh
- Once the database has been initialised, the App works offline (no graphics in offline-mode though)
  Thus not only favourite movies are stored in the database but all movies which were available when
  the movie list was refreshed for the last time.

Features planned in future releases
-----------------------------------

- Extend Picasso so that cached imaged are available in offline-mode
- In tablet-view update the grid when a movie is removed from favourites in the detail fragment.
- Fetch movie categories via the API
