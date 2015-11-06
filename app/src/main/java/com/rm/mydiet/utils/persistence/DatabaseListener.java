package com.rm.mydiet.utils.persistence;

import java.util.Collection;

/**
 * Created by alex
 */
public interface DatabaseListener {
    void onReceiveData(Collection<?> data);
    void onError(Exception e);
}
