package com.software.ssp.erkc

object Constants {

    const val SERVER_URL = "http://api.vc.tom.ru"
    const val API_MAIN_ENDPOINT_URL = "$SERVER_URL/apps/"
    const val API_OAUTH_URL = "http://api.vc.tom.ru/oauth/authorize/"
    const val API_OAUTH_CLIENT_ID = "VtqDyaekN"
    const val API_OAUTH_RESPONSE_TYPE = "token"
    const val API_OAUTH_REDIRECT_URI = "http://api.vc.tom.ru/auth/success.html"

    const val KEY_SELECTED_DRAWER_ITEM = "selected_drawer_item_key"

    const val KEY_SCAN_RESULT = "KEY_SCAN_RESULT"

    // Request codes
    const val REQUEST_CODE_BARCODE_SCAN = 12401

    const val KEY_CACHE_ADDRESSES_DATE = "cache_addresses_date"
    const val KEY_SELECTED_CARD_ITEM = "selected_card_item_key"
    const val KEY_ADDRESS_FIND_RESULT = "KEY_ADDRESS_FIND_RESULT"
    const val KEY_ADDRESS_NAME_RESULT = "KEY_ADDRESS_NAME_RESULT"
    const val REQUEST_CODE_ADDRESS_FIND = 12402
}
