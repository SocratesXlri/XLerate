package com.xldock.network;

/**
 * Created by honey on 10/10/17.
 */

public interface NetworkUtility {

    String BASE_URL="http://acad.xlri.ac.in/aisapp";

    interface URLS {
        String LOGIN = BASE_URL + "/bi/authenticate.php";

    }

    interface TAGS {
        String response = "success";
        String uid = "uid";
        String pwd = "pwd";

    }


}
