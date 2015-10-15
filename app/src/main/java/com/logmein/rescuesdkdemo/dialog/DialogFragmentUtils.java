package com.logmein.rescuesdkdemo.dialog;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Utility class to help DialogFragment transactions.
 */
public final class DialogFragmentUtils {

    private DialogFragmentUtils() {

    }

    /**
     * Returns the Fragment instance for the given tag.
     * @param manager The FragmentManager instance to provide access to fragment APIs.
     * @param tagToFind The unique string identifier assigned to the fragment.
     * @return The previous Fragment instance of null.
     */
    public static Fragment getFragmentByTag(final FragmentManager manager, final String tagToFind) {
        return manager.findFragmentByTag(tagToFind);
    }

    /**
     * Dismiss the previous fragment of the same tag and show the supplied DialogFragment.
     * @param manager The FragmentManager instance to provide access to fragment APIs.
     * @param fragment The DialogFragment instance to be shown.
     * @param tag The unique string tag assigned to the fragment.
     */
    public static void showFragmentAndDismissPrevious(final FragmentManager manager, final DialogFragment fragment, final String tag) {
        final Fragment shownFragment = getFragmentByTag(manager, tag);
        if (shownFragment instanceof DialogFragment) {
            ((DialogFragment) shownFragment).dismiss();
        }
        fragment.show(manager, tag);
    }
}
