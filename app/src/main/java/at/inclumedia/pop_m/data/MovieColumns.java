package at.inclumedia.pop_m.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Martin Melcher on 06/11/15.
 */
public interface MovieColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull String TITLE = "title";
    @DataType(DataType.Type.TEXT) String PLOT = "plot";
    @DataType(DataType.Type.TEXT) String THUMB_URL = "thumb_url";
    @DataType(DataType.Type.REAL) String RATING = "rating";
    @DataType(DataType.Type.TEXT) String RELEASE_DATE = "release_date";
    @DataType(DataType.Type.INTEGER) String IS_POPULAR = "is_popular";
    @DataType(DataType.Type.INTEGER) String IS_HIRATED = "is_hirated";
    @DataType(DataType.Type.INTEGER) String IS_FAVOURITE = "is_favourite";
}
