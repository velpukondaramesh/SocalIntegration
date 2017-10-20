package com.zappstech.socalintegration.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ram on 10/20/2017.
 */

public class LoginResponse {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("user")
    @Expose
    private User user;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
