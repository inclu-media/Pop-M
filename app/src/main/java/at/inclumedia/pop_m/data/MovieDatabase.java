package at.inclumedia.pop_m.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Martin Melcher on 06/11/15.
 */

@Database(version = MovieDatabase.VERSION)
public final class MovieDatabase {

    public static final int VERSION = 1;

    private MovieDatabase() {}

    @Table(MovieColumns.class) public static final String MOVIES = "movies";
    @Table(TrailerColumns.class) public static final String TRAILERS = "trailers";
    @Table(ReviewColumns.class) public static final String REVIEWS = "reviews";
}
