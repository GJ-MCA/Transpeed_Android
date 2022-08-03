package com.vpg.transpeed.ApiManager;

public class WebURL {

    //local url
    private static final String IP_ADDRESS = "192.168.184.66";
    public static final String MAIN_URL = "http://" + IP_ADDRESS + "/Transpeed/APIs/";

    //registration api insert
    public static final String SIGN_UP_URL = MAIN_URL + "signup.php";

    //log in api url select
    public static final String SIGN_IN_URL = MAIN_URL + "signin.php";

    //user type api url select
    public static final String USER_TYPE_URL = MAIN_URL + "userType.php";

    //user type api url select
    public static final String CUSTOMER_PROFILE_URL = MAIN_URL + "profile.php";

    //profile update api update
    public static final String CUSTOMER_PROFILE_UPDATE_URL = MAIN_URL + "profileUpdate.php";

    //password change api update
    public static final String PASSWORD_CHANGE_URL = MAIN_URL + "passwordChange.php";

    //city api url select
    public static final String CITY_URL = MAIN_URL + "city.php";

    //area by city api select
    public static final String AREA_URL = MAIN_URL + "area.php";

    //create new order api insert
    public static final String DISTANCE_URL = MAIN_URL + "distance.php";

    //create new order api insert
    public static final String NEW_ORDER_URL = MAIN_URL + "newOrder.php";

    //create new order api select
    public static final String TIME_SLOT_URL = MAIN_URL + "timeSlot.php";

    //create new order api select
    public static final String ITEM_TYPE_URL = MAIN_URL + "itemType.php";

    //create my orders list api select
    public static final String MY_ORDERS_URL = MAIN_URL + "myOrdersList.php";

    //create my order details api select
    public static final String MY_ORDER_DETAILS_URL = MAIN_URL + "myOrderDetails.php";

    //create my order details api select
    public static final String CANCEL_MY_ORDER_URL = MAIN_URL + "cancelOrder.php";

    //send otp to user email api
    public static final String SEND_MAIL_URL = MAIN_URL + "sendMail.php";

    //send reset password otp to user email api
    public static final String SEND_RESET_MAIL_URL = MAIN_URL + "forgotPasswordMail.php";

    //send reset password otp to user email api
    public static final String RESET_PASSWORD_URL = MAIN_URL + "resetPassword.php";

    //send otp to user mobile api
    public static final String SEND_MOBILE_OTP_URL = MAIN_URL + "sendMobileOTP.php";

    //check order id exist or not select api
    public static final String CHECK_ORDER_ID_URL = MAIN_URL + "checkOrderId.php";

    //order tracking api
    public static final String ORDER_TRACK_URL = MAIN_URL + "orderTracking.php";


}
