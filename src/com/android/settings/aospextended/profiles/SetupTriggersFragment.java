/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.aospextended.profiles;

import android.annotation.Nullable;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.lineageos.app.Profile;
import com.android.internal.util.lineageos.app.ProfileManager;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SubSettings;
import com.android.settings.aospextended.profiles.triggers.NfcTriggerFragment;

public class SetupTriggersFragment extends SettingsPreferenceFragment {

    ViewPager mPager;
    Profile mProfile;
    ProfileManager mProfileManager;
    TriggerPagerAdapter mAdapter;
    boolean mNewProfileMode;
    int mPreselectedItem;

    public static final String EXTRA_INITIAL_PAGE = "current_item";

    private static final int REQUEST_SETUP_ACTIONS = 5;

    public static SetupTriggersFragment newInstance(Profile profile, boolean newProfile) {
        SetupTriggersFragment fragment = new SetupTriggersFragment();
        Bundle args = new Bundle();
        args.putParcelable(ProfilesSettingsFragment.EXTRA_PROFILE, profile);
        args.putBoolean(ProfilesSettingsFragment.EXTRA_NEW_PROFILE, newProfile);
        fragment.setArguments(args);
        return fragment;
    }

    public SetupTriggersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProfile = getArguments().getParcelable(ProfilesSettingsFragment.EXTRA_PROFILE);
            mNewProfileMode = getArguments().getBoolean(ProfilesSettingsFragment.EXTRA_NEW_PROFILE, false);
            mPreselectedItem = getArguments().getInt(EXTRA_INITIAL_PAGE, 0);
        }
        mProfileManager = ProfileManager.getInstance(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            if (mNewProfileMode) {
                actionBar.setTitle(R.string.sp_profile_setup_setup_triggers_title);
            } else {
                String title = getString(R.string.sp_profile_setup_setup_triggers_title_config,
                        mProfile.getName());
                actionBar.setTitle(title);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPager.setCurrentItem(mPreselectedItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean aBoolean = getResources().getBoolean(R.bool.theme_dark);
        Context contextThemeWrapper;
        if(aBoolean)
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.TabAppThemeBaseDark);
        else
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.TabAppThemeBase);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View root = localInflater.inflate(R.layout.fragment_setup_triggers, container, false);

        mPager = (ViewPager) root.findViewById(R.id.view_pager);
        mAdapter = new TriggerPagerAdapter(getActivity(), getChildFragmentManager());

        Bundle profileArgs = new Bundle();
        profileArgs.putParcelable(ProfilesSettingsFragment.EXTRA_PROFILE, mProfile);

        final TriggerPagerAdapter.TriggerFragments[] fragments =
                TriggerPagerAdapter.TriggerFragments.values();

        for (final TriggerPagerAdapter.TriggerFragments fragment : fragments) {
            if (fragment.getFragmentClass() == NfcTriggerFragment.class) {
                if (!getActivity().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_NFC)) {
                    // device doesn't have NFC
                    continue;
                }
            }
            mAdapter.add(fragment.getFragmentClass(), profileArgs, fragment.getTitleRes());
        }

        mPager.setAdapter(mAdapter);

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

/*        PagerTabStrip tabs = (PagerTabStrip) root.findViewById(R.id.tabs);
        TypedValue colorAccent = new TypedValue();
        getPrefContext().getTheme().resolveAttribute(com.android.internal.R.attr.colorAccent,
                colorAccent, true);
        tabs.setTabIndicatorColorResource(colorAccent.resourceId);*/

        if (mNewProfileMode) {
            if(hasNextButton()){
                getNextButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle args = new Bundle();
                        args.putParcelable(ProfilesSettingsFragment.EXTRA_PROFILE, mProfile);
                        args.putBoolean(ProfilesSettingsFragment.EXTRA_NEW_PROFILE, mNewProfileMode);
                        args.putBoolean(SettingsActivity.EXTRA_PREFS_SHOW_BUTTON_BAR, true);

                        //startFragment(SetupTriggersFragment.this, SetupActionsFragment.class.getCanonicalName(), R.string.sp_profile_profile_manage, REQUEST_SETUP_ACTIONS,args);
                        startActivityForResult(SetupActionsFragment.class.getCanonicalName(), REQUEST_SETUP_ACTIONS, args, R.string.sp_profile_profile_manage);
                    }
                });
            }
        }
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETUP_ACTIONS) {
            if (resultCode == Activity.RESULT_OK) {
                // exit out of the wizard!
                finishFragment();
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYSTEM_PROFILES;
    }

    @Override
    public int getDialogMetricsCategory(int dialogId) {
        return MetricsProto.MetricsEvent.SYSTEM_PROFILES;
    }

    private void startActivityForResult(String fragmentClass, int requestCode, Bundle args, int titleRes){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(getPrefContext(), SubSettings.class);
        if(args.containsKey(SettingsActivity.EXTRA_PREFS_SHOW_BUTTON_BAR)){
            intent.putExtra(SettingsActivity.EXTRA_PREFS_SHOW_BUTTON_BAR, args.getBoolean(SettingsActivity.EXTRA_PREFS_SHOW_BUTTON_BAR, false));
        }
        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, fragmentClass);
        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT_TITLE_RESID, titleRes);
        intent.putExtra(SettingsActivity.EXTRA_SOURCE_METRICS_CATEGORY, getMetricsCategory());

        startActivityForResult(intent, requestCode);
    }
}