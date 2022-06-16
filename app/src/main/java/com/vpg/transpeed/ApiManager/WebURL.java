package com.vpg.transpeed.ApiManager;

public class WebURL {

    //local url
    private static final String IP_ADDRESS = "192.168.155.66";
    public static final String MAIN_URL = "http://" + IP_ADDRESS + "/Transpeed/APIs/";

    //registration api insert
    public static final String SIGN_UP_URL = MAIN_URL + "signup.php";

    //log in api url select
    public static final String SIGN_IN_URL = MAIN_URL + "signin.php";

    //user type api url select
    public static final String USER_TYPE_URL = MAIN_URL + "userType.php";

}
