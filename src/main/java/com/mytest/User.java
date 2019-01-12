package com.mytest;

import com.sun.istack.internal.NotNull;

import java.util.HashSet;
import java.util.Set;

public class User {

    private String userId;
    private String userName;
    private Set<String> userCity = new HashSet<>();


    private User(@NotNull final String userName, @NotNull final String userCity, @NotNull final String userId){
        setUserId(userId);
        setUserName(userName);
        addUserCity(userCity);
    }

    public static User addUserData(@NotNull final String userName, @NotNull final String userCity, @NotNull final String userId){
        return new User(userName, userCity, userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        User u = (User) obj;
        return this.userId.equals(u.userId);
    }

    public String getUserId() {
        return userId;
    }

    private void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    private void setUserName(final String userName) {
        this.userName = userName;
    }

    public Set<String> getUserCities() {
        return userCity;
    }

    public void addUserCity(final String userCity) {
        this.userCity.add(userCity);
    }

    @Override
    public String toString() {
        return "com.mytest.User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userCity=" + userCity +
                '}';
    }
}
