package com.rd.yunlib.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

/**
 * 登录请求结果
 *
 * @author D10NG
 * @date on 2020/10/10 11:39 AM
 */
@JsonClass(generateAdapter = true)
data class LoginResult(
    var code: Int = 200,
    var msg: String = "",
    var data: Data = Data()
): Serializable {
    @JsonClass(generateAdapter = true)
    data class Data (
        @Json(name = "user_avatar")
        var userAvatar: String = "",

        @Json(name = "user_nick")
        var userNick: String = "",

        @Json(name = "user_session")
        var userSession: String = "",

        @Json(name = "user_uuid")
        var userUuid: String = ""
    ): Serializable
}