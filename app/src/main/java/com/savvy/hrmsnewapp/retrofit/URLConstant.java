package com.savvy.hrmsnewapp.retrofit;

import com.savvy.hrmsnewapp.utils.Constants;

public class URLConstant {
    public static final String BASE_URL = Constants.IP_ADDRESS+"/";
    public static final String URL_MENU_DATA_REQUEST = "/savvymobile/SavvyMobileService.svc/GetLoggedUserPrivileges/";
    public static final String URL_PROFILE_DATA_REQUEST = "/savvymobile/SavvyMobileService.svc/EmployeeProfilePostDynamic";
    public static final String URL_SERVER_DATE_TIME_REQUEST = "/savvymobile/SavvyMobileService.svc//GetCurrentDateTime";
    public static final String URL_SHORT_LEAVE_APPROVAL_URL = "/savvymobile/SavvyMobileService.svc//GetShortLeaveChangeRequestDetail/{empId}";

    public static final String URL_SERVER_DATE_TIME_WITH_BUTTON = "/savvymobile/SavvyMobileService.svc//GetCurrentDateTimeWithEnableDisableButton";
}
