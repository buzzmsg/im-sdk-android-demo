package com.tmmtmm.demo.exception

/**
 * @description
 *
 * @time 2021/5/17 9:03 下午
 * @version
 */
enum class TmmError(var code: Int, var message: String) {

    ERROR_UID_EMPTY(102, "UID can not be empty"),
    ERROR_OTHER_EXCEPTION(188, "UID can not be empty"),
    ERROR_COMMON(500, "error_unknown"),
    ERROR_TOKEN(401, ""),
    ERROR_SERVER(400, ""),
    ERROR_USER_DELETE(600, ""),
    ERROR_NETWORK(999, "net_something");

    companion object {
        fun getDec(code:Int):String {
            return when (code) {
                ERROR_UID_EMPTY.code -> {
                    ERROR_UID_EMPTY.message
                }

                ERROR_OTHER_EXCEPTION.code -> {
                    ERROR_OTHER_EXCEPTION.message
                }

                ERROR_COMMON.code -> {
//                    TmUtils.sApp.getString(R.string.error_unknown)
                    "error_unknown"
                }
                ERROR_TOKEN.code -> {
                    ERROR_TOKEN.message
                }

                ERROR_NETWORK.code -> {
//                    TmUtils.sApp.getString(R.string.net_something)
                    "net_something"
                }
                else -> {
//                    TmUtils.sApp.getString(R.string.error_unknown)
                    "error_unknown"
                }
            }
        }
    }



}