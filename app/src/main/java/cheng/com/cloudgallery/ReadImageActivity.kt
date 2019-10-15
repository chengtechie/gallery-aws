package cheng.com.cloudgallery

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_read_image.*
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception

class ReadImageActivity : AppCompatActivity() { // testing read image from storage feature

    private val tag = "Read_Image_Activity"
    private val loadImageReqCode = 1
    private var imageUriList = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_image)

    }

    fun onReadClicked(view: View) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        photoPickerIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), loadImageReqCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == loadImageReqCode && resultCode == Activity.RESULT_OK) {
                if (data!!.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        Log.d(tag, "ImageUri (If): $imageUri")
                        imageUriList.add(imageUri)
                    }
                } else {
                    val imagePath = data.data!!.path
                    val imageUri = Uri.fromFile(File(imagePath))
                    Log.d(tag, "ImageUri (Else): $imageUri")
                    imageUriList.add(imageUri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var imageStream = contentResolver.openInputStream(imageUriList[0])
        var selectedImage = BitmapFactory.decodeStream(imageStream)
        image1.setImageBitmap(selectedImage)
        imageStream = contentResolver.openInputStream(imageUriList[1])
        selectedImage = BitmapFactory.decodeStream(imageStream)
        image2.setImageBitmap(selectedImage)
    }
}
