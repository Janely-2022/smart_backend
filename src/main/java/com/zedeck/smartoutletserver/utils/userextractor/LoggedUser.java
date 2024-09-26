package com.zedeck.smartoutletserver.utils.userextractor;


import com.zedeck.smartoutletserver.model.UserAccount;

public interface LoggedUser {

    UserInfo getInfo();

    UserAccount getUser();
}
