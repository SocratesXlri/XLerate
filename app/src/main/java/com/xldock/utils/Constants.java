package com.xldock.utils;

/**
 * Created by Honey Shah on 18-11-2017.
 */

public class Constants {

    //server URLs
    public static String LIVE_URL="http://acad.xlri.ac.in/aisapp";
    public static String LIVE_FOLDER="aitemp";
    public static String DEBUG_URL="";

    //form URLs
    public static final String LAPTOP_ISSUE_FORM_URL="https://docs.google.com/forms/d/e/1FAIpQLSdXHlDoO-3l3E9CO0Ek3Z8RYV1g9PWuTDUDYKpTSKPJQ0mxIA/viewform?usp=sf_link";
    public static final String INTERNET_COMPLAINTS_URL="https://docs.google.com/forms/d/e/1FAIpQLSdpsmht_CQPeEnSOnv77YVj-QPUHSOFI_qom-PQE8V6FE47rg/viewform?usp=sf_link";
    public static final String MAC_ID_REGISTERATION_URL="https://docs.google.com/forms/d/e/1FAIpQLScLMw2jAUkWyTgJe-VKmCGp90NEvQ-0ZQFL6zNOurtXI-YtJw/viewform?usp=sf_link";
    public static final String INFRACOM_ISSUES_FORM_URL=" https://docs.google.com/forms/d/e/1FAIpQLScGMn2EU-s8SlZxvHnRd2iPNcNBdDX67TWUHK-k8DYgIUq8zw/viewform";

    //others
    public static final String FROM ="from";
    public static final String DATA="data";

    //for admin login --> username and password
    public static String ADMIN_USERNAME="admin";
    public static String ADMIN_PASSWORD="admin@123";

    //web services end points
    private static String GRADES_URL= "my-grades.php?SID=";
    private static String MY_SCHEDULE_URL= "my-schedule.php?SID=";
    private static String COURSE_SCHEDULE_URL= "course-list.php";
    private static String SEARCH_URL="search.php";
    public static String CHARMS_URL= "http://charms.tk/";


    public static String getGradesUrl(String url, String baseFolder) {
        return url+"/"+baseFolder+"/"+GRADES_URL;
    }

    public static String getMyScheduleUrl(String url, String baseFolder) {
        return url+"/"+baseFolder+"/"+MY_SCHEDULE_URL;
    }

    public static String getCourseScheduleUrl(String url, String baseFolder) {
        return url+"/"+baseFolder+"/"+COURSE_SCHEDULE_URL;
    }

    public static String getSearchUrl(String url, String baseFolder) {
        return url+"/"+baseFolder+"/"+SEARCH_URL;
    }
}
