UADN Assignment 1: Popular Movies (Pop-M)
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

- ButterKnife
