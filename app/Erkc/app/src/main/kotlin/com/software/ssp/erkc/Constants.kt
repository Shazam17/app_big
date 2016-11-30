package com.software.ssp.erkc

object Constants {

    const val SERVER_URL = "http://api.vc.tom.ru"
    const val API_MAIN_ENDPOINT_URL = "$SERVER_URL/apps/"
    const val API_OAUTH_URL = "http://api.vc.tom.ru/oauth/authorize/"
    const val API_OAUTH_CLIENT_ID = "VtqDyaekN"
    const val API_OAUTH_RESPONSE_TYPE = "token"
    const val API_OAUTH_REDIRECT_URI = "http://api.vc.tom.ru/auth/success.html"

    //DateTime formats
    const val PERIOD_DATE_FORMAT_API = "yyyyMM"
    const val PERIOD_DATE_FORMAT_UI = "MMM yyyy"
    const val DATE_TIME_FORMAT_API = "yyyy-MM-dd hh:mm:ss"
    const val HISTORY_DATE_FORMAT = "dd MMM"
    const val RECEIPT_DATE_FORMAT = "dd MMM yyyy"

    // Request codes
    const val REQUEST_CODE_PAYMENT = 12403

    // Keys
    const val KEY_SELECTED_CARD_ITEM = "selected_card_item_key"
    const val KEY_URL = "url_key"
    const val KEY_URL_ACTIVITY_TITLE = "url_activity_title_key"
    const val KEY_RECEIPT = "KEY_RECEIPT"
    const val KEY_URL_RESULT = "URL_RESULT_KEY"
    const val KEY_SELECTED_DRAWER_ITEM = "selected_drawer_item_key"

    const val KEY_DRAWER_ITEM_FOR_SELECT = "drawner_item_for_select_key"
    const val KEY_PAYMENT = "KEY_PAYMENT"
}
