package com.example.thanksd.login.dataclass
object ClientInformation {
    var token: String = "default"
    var email: String? = null
    var isRegistered = false
    var platformID = "no-info"

    fun updateValue(token:String,email:String?,isRegistered:Boolean,platformID:String) {
        this.token = token
        this.email = email
        this.isRegistered = isRegistered
        this.platformID = platformID
    }
}
