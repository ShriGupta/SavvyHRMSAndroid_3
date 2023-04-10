package com.savvy.hrmsnewapp.helper;

/**
 * Created by savvy on 5/21/2018.
 */

public interface AsyncTaskCallback<T> {
    void execute(T data);
}
