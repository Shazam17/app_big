package com.software.ssp.erkc

object Constants {
    //const val DEBUG_SIMULATE_ALL_USER_ADDED_IPUS = false
    const val DEBUG_OFFLINE_MODE = false
    const val V_API_MAIN = "https://api.vc.tom.ru"
    const val V_API_TEST = "https://testapi.vc.tom.ru"
    const val V_API = V_API_MAIN //TODO: change in release

    const val SERVER_URL = V_API
    const val API_MAIN_ENDPOINT_URL = "$SERVER_URL/apps/"
    const val API_OAUTH_URL = "$V_API/oauth/authorize/"
    const val API_OAUTH_CLIENT_ID = "VtqDyaekN"
    const val API_OAUTH_RESPONSE_TYPE = "token"
    const val API_OAUTH_REDIRECT_URI = "$V_API/auth/success.html"
    const val API_SIG_PRIVATE_KEY = "21ba15f3410d6fe7fd00b83d18025358"

    // FOR WEB REDIRECT
    const val BANK_URL = "www.gazprombank.ru"
    const val STORE_URL = "http://vc.tom.ru/"
    //DateTime formats
    const val PERIOD_DATE_FORMAT_API = "yyyyMM"
    const val PERIOD_DATE_FORMAT_UI = "MMM yyyy"
    const val DATE_TIME_FORMAT_PAYMENTS_UI = "dd MMM yyyy, HH:mm"
    const val DATE_TIME_FORMAT_API = "yyyy-MM-dd HH:mm:ss"
    const val DATE_TIME_FORMAT_API_PAYMENTS = "dd.MM.yyyy HH:mm"
    const val HISTORY_DATE_FORMAT = "dd MMM yyyy"
    const val RECEIPT_DATE_API_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val RECEIPT_DATE_FORMAT = "dd MMM yyyy"
    const val VALUES_DATE_FORMAT = "dd.MM.yyyy"
    const val NOTIFICATIONS_FORMAT_API = "dd.MM.yyyy HH:mm:ss"
    const val NOTIFICATIONS_DATETIME_FORMAT = "dd MMM\nHH:mm"
    const val NOTIFICATIONS_DATETIME_FORMAT_SIMPLE = "HH:mm"
    const val NOTIFICATIONS_SCREEN_DATETIME_FORMAT = "dd.MM.yyyy HH:mm"

    // Keys
    const val KEY_SELECTED_CARD_ITEM = "selected_card_item_key"
    const val KEY_URL = "url_key"
    const val KEY_URL_ACTIVITY_TITLE = "url_activity_title_key"
    const val KEY_RECEIPT = "KEY_RECEIPT"
    const val KEY_IPU_NUMBER = "KEY_IPU_NUMBER"
    const val KEY_HISTORY_FILTER = "KEY_HISTORY_FILTER"
    const val KEY_SELECTED_DRAWER_ITEM = "selected_drawer_item_key"
    const val KEY_PAYMENT = "KEY_PAYMENT"
    const val KEY_FROM_TRANSACTION = "KEY_FROM_TRANSACTION"
    const val KEY_NOTIFICATION_TYPE = "KEY_NOTIFICATION_TYPE"
    const val KEY_NOTIFICATION_ID = "notificationId"

    // Т.к. нет у АПИ кодов для определения горячая вода / холодная вода / эл-во приняли решение привязаться в названию
    const val HOT_WATER = "г/в"
    const val COLD_WATER = "х/в"
    const val GAS = "газ"
    const val ELECTRICITY = "э/эл"

    const val NOTIFICATION_ACTION_CLICK = "notificationClicked"
}
