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

package com.android.settings.aospextended.profiles.actions.item;

import com.android.settings.R;
import com.android.settings.aospextended.profiles.actions.ItemListAdapter;

import com.android.internal.util.lineageos.app.Profile;

public class NotificationLightModeItem extends BaseItem {
    Profile mProfile;

    public NotificationLightModeItem(Profile profile) {
        mProfile = profile;
    }

    @Override
    public ItemListAdapter.RowType getRowType() {
        return ItemListAdapter.RowType.NOTIFICATIONLIGHTMODE_ITEM;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getTitle() {
        return getString(R.string.sp_notification_light_title);
    }

    @Override
    public String getSummary() {
        return getString(getSummaryString(mProfile));
    }

    public static int getSummaryString(Profile profile) {
        switch (profile.getNotificationLightMode()) {
            case Profile.NotificationLightMode.DEFAULT:
                return R.string.sp_profile_action_none; //"leave unchanged"
            case Profile.NotificationLightMode.ENABLE:
                return R.string.sp_profile_action_enable;
            case Profile.NotificationLightMode.DISABLE:
                return R.string.sp_profile_action_disable;
            default: return 0;
        }
    }
}