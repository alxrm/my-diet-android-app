package com.rm.mydiet.utils.persistence.listeners;

import com.rm.mydiet.utils.UserPermissionListener;

/**
 * Created by alex
 */
public interface DatabaseUpdateListener {
    void onUpdateAvailable(UserPermissionListener listener);
    void onLoadComplete();
    void onLoadStarted();
}
