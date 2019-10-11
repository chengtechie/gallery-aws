package cheng.com.cloudgallery

class Picture(private val id: Int, private val ut: String, private val desc: String) {
    var drawableId = id
    var uploadTime = ut
    var description = desc
}