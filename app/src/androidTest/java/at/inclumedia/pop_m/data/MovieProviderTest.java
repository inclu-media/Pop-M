package at.inclumedia.pop_m.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.test.AndroidTestCase;


/**
 * Created by Martin Melcher on 06/11/15.
 */
public class MovieProviderTest extends AndroidTestCase {

    private static final String LOG_TAG = MovieProviderTest.class.getSimpleName();

    /**
     *  This test checks to make sure that the content provider is registered correctly.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        ProviderInfo providerInfo = pm.resolveContentProvider(MovieProvider.AUTHORITY, 0);
        assertTrue("MovieProvider successfully registered.", providerInfo != null);
    }

    /**
     *  Ckeck that movies ca be inserted and deleted via the content provider
     */
}
