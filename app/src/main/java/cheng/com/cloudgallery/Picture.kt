package cheng.com.cloudgallery

import android.graphics.Bitmap

class Picture() {

    var drawableId: Int? = null
    var uploadTime: String? = null
    var description: String? = null
    var bitmap: Bitmap? = null

    constructor(id: Int, ut: String, desc: String): this() {
        drawableId = id
        uploadTime = ut
        description = desc
    }

    constructor(bm: Bitmap, ut: String, desc: String): this(){
        bitmap = bm
        uploadTime = ut
        description = desc
    }

}