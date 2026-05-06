package com.projects.pogodski;

public interface LocationCallback {
    void onSuccess(String city);
    void onError(String error);
}
