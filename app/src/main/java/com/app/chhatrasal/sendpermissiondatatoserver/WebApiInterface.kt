package com.app.chhatrasal.sendpermissiondatatoserver

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.PUT
import java.util.*

interface WebApiInterface {

    @PUT("/sendData")
    fun sendData(@Body map: HashMap<String, Any>): Observable<RequestData>
}