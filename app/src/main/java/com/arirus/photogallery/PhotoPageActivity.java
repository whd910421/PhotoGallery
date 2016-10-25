package com.arirus.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by whd910421 on 16/10/22.
 */

public class PhotoPageActivity extends SingleFragmentActivity {
    private static PhotoPageFragment mFragment;
    public static Intent newIntent(Context context, Uri uri)
    {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(uri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mFragment;
    }

    @Override
    public void onBackPressed() {
        if(mFragment.canGoBack())
            mFragment.goBack();
        else
            super.onBackPressed();
    }
}
