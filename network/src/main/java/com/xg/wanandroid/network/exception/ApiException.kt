package com.xg.wanandroid.network.exception

class ApiException(val errorCode: Int, val errorMsg: String): RuntimeException() {
}