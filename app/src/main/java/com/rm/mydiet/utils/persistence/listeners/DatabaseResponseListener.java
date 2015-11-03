package com.rm.mydiet.utils.persistence.listeners;

import java.util.Collection;

/**
 * Created by alex
 */
public interface DatabaseResponseListener {
    void onReceiveData(Collection<?> data);
    void onError(Exception e);
}
