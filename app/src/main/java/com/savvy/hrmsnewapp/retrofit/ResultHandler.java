package com.savvy.hrmsnewapp.retrofit;

public interface ResultHandler<T> {
    void onSuccess(T data);
    void onFailure(String message);
}
