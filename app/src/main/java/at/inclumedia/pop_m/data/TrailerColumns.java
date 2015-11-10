package at.inclumedia.pop_m.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by Martin Melcher on 07/11/15.
 */
public interface TrailerColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(DataType.Type.INTEGER) @NotNull @References(table = MovieDatabase.MOVIES, column = MovieColumns._ID)
    String MOVIE_ID = "movie_id";
    @DataType(DataType.Type.TEXT) String TYPE = "type";
    @DataType(DataType.Type.TEXT) @NotNull String NAME = "name";
    @DataType(DataType.Type.TEXT) @NotNull String YOUTUBE_URL = "youtube_url";
}
