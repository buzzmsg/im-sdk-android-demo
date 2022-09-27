package com.tmmtmm.demo.exception


import com.tmmtmm.demo.api.ResponseResult
import java.io.IOException
import java.net.*

class TmException : RuntimeException {
    var code: Int? = 0

    constructor(code: Int?, message: String?) : super(message) {
        this.code = code
    }

    constructor(message: String?) : super(message) {
    }

    constructor(error: TmmError) : super(error.message) {
        this.code = error.code
    }

    companion object {
        private val networkExceptionList = mutableListOf(
            IOException::class,
            SocketException::class,
        )


        fun common(): TmException {
            return TmException(
                TmmError.ERROR_COMMON.code,
                TmmError.getDec(TmmError.ERROR_COMMON.code)
            )
        }

        fun <T : Exception> parse(e: Exception): ResponseResult<T> {
            return when (e.javaClass) {
                IOException::class, SocketException::class -> {
                    ResponseResult.Failure(
                        TmException(
                            TmmError.ERROR_NETWORK.code,
                            TmmError.getDec(TmmError.ERROR_NETWORK.code)
                        )
                    )
                }

//                {
//                    ResponseResult.Failure(TmException(TmmError.ERROR_NETWORK.code,TmmError.getDec(TmmError.ERROR_NETWORK.code)))
//
//                }

                else -> ResponseResult.Failure(
                    TmException(
                        TmmError.ERROR_COMMON.code,
                        TmmError.getDec(TmmError.ERROR_COMMON.code)
                    )
                )
            }
        }
    }


}