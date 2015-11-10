package at.inclumedia.pop_m.mdbapi;

import android.net.Uri;

/**
 * Created by Martin Melcher on 07/11/15.
 */
public class Trailer {

    public String type;
    public String name;
    public Uri youTubeUri;

    public Trailer(String type, String name, Uri youTubeUri) {
        this.type = type;
        this.name = name;
        this.youTubeUri = youTubeUri;
    }
}
