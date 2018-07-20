package com.zeropercenthappy.retrofitutil

enum class ContentType(val value: String) {
    CONTENT_TYPE("Content-Type"),
    TEXT("text/plain"),
    IMAGE("image/*"),
    JPG("image/jpeg"),
    PNG("image/png"),
    MP3("audio/mp3"),
    WAV("audio/wav"),
    WMA("audio/x-ms-wma"),
    MP4("video/mpeg4"),
    AVI("video/avi"),
    WMV("video/x-ms-wmv"),
    JSON("application/json")
    ;
}