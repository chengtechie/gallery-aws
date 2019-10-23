package cheng.com.cloudgallery

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_edit_file.*
import kotlinx.android.synthetic.main.fragment_picture.*
import kotlinx.android.synthetic.main.picture_item.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var pictures = ArrayList<Picture>()
    private var bundle = Bundle()
    private val tag = "Main_Activity"
    private val loadImageReqCode = 1
    private var imageUri: Uri? = null
    private val deleteActionFragment = DeleteActionFragment()
    private val editActionFragment = EditActionFragment()
    private lateinit var viewAdapter: MyRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        startService(Intent(this, TransferService::class.java))

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);

        AWSMobileClient.getInstance().initialize(this, object: Callback<UserStateDetails> {
            override fun onResult(result: UserStateDetails?) {
//                AWSMobileClient.getInstance().signOut()
                Log.i(tag, "onResult ${result!!.userState}")
            }

            override fun onError(e: Exception?) {
                Log.e(tag, "Initialization error", e)
            }
        })

        uploadBtn.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            photoPickerIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), loadImageReqCode)

        }

//        setDummyPics()

        recyclerView = findViewById(R.id.picturesRecyclerView)
    }

    override fun onStart() {
        super.onStart()

        viewAdapter = MyRecyclerAdapter(pictures)

//        Log.d(tag, "List size: ${pictures.size}")

        viewAdapter.setOnItemClickListener(object: MyRecyclerAdapter.MyClickListener {
            override fun onItemClicked(position: Int, v: View) {
//                Toast.makeText(this@MainActivity, "Position is $position", Toast.LENGTH_SHORT).show() // testing
//                bundle.putParcelable("centerImage", pictures[position].bitmap)
//                val pictureFragment = PictureFragment()
//                pictureFragment.arguments = bundle
//                supportFragmentManager.beginTransaction() // show the most recent fragment with the correct data
//                    .replace(R.id.fragmentContainer, pictureFragment).commit()
//                fragmentContainer.visibility = View.VISIBLE

            }

            override fun onLongItemClicked(position: Int, v: View) {
//                Toast.makeText(this@MainActivity, "Position (long) is $position", Toast.LENGTH_SHORT).show() // testing
                bundle.putInt("position", position + 1) // pass data to fragment using intent
                val fileActionFragment = FileActionFragment()
                fileActionFragment.arguments = bundle
                supportFragmentManager.beginTransaction() // show the most recent fragment with the correct data
                    .replace(R.id.fragmentContainer, fileActionFragment).commit()
                fragmentContainer.visibility = View.VISIBLE
            }
        })

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = viewAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == loadImageReqCode && resultCode == Activity.RESULT_OK) {
                imageUri = data!!.data!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val imageStream = contentResolver.openInputStream(imageUri)
        val imageBitmap = BitmapFactory.decodeStream(imageStream)
        val output = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val path = MediaStore.Images.Media.insertImage(applicationContext.contentResolver, imageBitmap, "Title", null)
            val newUri = Uri.parse(path)
            Log.d(tag, "new URI : $newUri")
            imageUri = newUri
        }
        pictures.add(Picture(imageBitmap, Calendar.getInstance().time.toString(), "No description yet"))
        onRestart()
    }

    fun onCancelClicked(view: View) {
        fragmentContainer.visibility = View.GONE
    }

    fun onDeleteClicked(view: View) {
        deleteActionFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, deleteActionFragment).commit()
    }

    fun onRealDeleteClicked(view: View) {
        pictures.removeAt(bundle.getInt("position") - 1)
        deleteActionFragment.dismiss()
        viewAdapter.notifyDataSetChanged()
    }

    fun onEditClicked(view: View) {
        editActionFragment.arguments = bundle
//        Toast.makeText(this@MainActivity, "Position ${bundle.getInt("position")}", Toast.LENGTH_SHORT).show()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, editActionFragment).commit()
    }

    fun onRealEditClicked(view: View) {
        pictures[bundle.getInt("position") - 1].description = inputDescription.text.toString()
        pictures[bundle.getInt("position") - 1].updateTime = Calendar.getInstance().time.toString()
//        Toast.makeText(this@MainActivity, "Position ${bundle.getInt("position")}", Toast.LENGTH_SHORT).show()
        editActionFragment.dismiss()
        viewAdapter.notifyDataSetChanged()
    }

    fun onAwsUploadClicked(view: View) {
        Toast.makeText(this, "Now uploading pictures to AWS S3", Toast.LENGTH_SHORT).show()
        UploadImage().execute()
    }

    private fun setDummyPics() { // test recycler view
        pictures.add(Picture(R.drawable.upload, "1:00pm", "This is picture 1"))
        pictures.add(Picture(R.drawable.upload, "2:30pm", "This is picture 2"))
        pictures.add(Picture(R.drawable.upload, "3:45pm", "This is picture 3"))
        pictures.add(Picture(R.drawable.upload, "1:00pm", "This is picture 1"))
        pictures.add(Picture(R.drawable.upload, "2:30pm", "This is picture 2"))
        pictures.add(Picture(R.drawable.upload, "3:45pm", "This is picture 3"))
        pictures.add(Picture(R.drawable.upload, "1:00pm", "This is picture 1"))
        pictures.add(Picture(R.drawable.upload, "2:30pm", "This is picture 2"))
        pictures.add(Picture(R.drawable.upload, "3:45pm", "This is picture 3"))
    }

    inner class UploadImage: AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            uploadWithTransferUtility()
        }

        override fun onPostExecute(result: Unit?) {
            Toast.makeText(this@MainActivity, "AWS Upload Complete", Toast.LENGTH_SHORT).show()
        }
    }

    fun uploadWithTransferUtility() {
        Log.d(tag, "Inside Transfer Utility")
        val transferUtility = TransferUtility.builder()
            .context(applicationContext)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(AmazonS3Client(AWSMobileClient.getInstance()))
            .build()
        try {
            val file = File(getRealPathFromUri(imageUri!!))
            val uploadObserver = transferUtility.upload("cheng/Image${pictures.size}", file)
            uploadObserver.setTransferListener(object: TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    Log.d(tag, "Uploading to AWS")
                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    if (state == TransferState.COMPLETED) {
                        Log.d(tag, "AWS Upload complete")
                    }
                }

                override fun onError(id: Int, ex: Exception?) {
                    Log.e(tag, "Upload Observer error")
                    Log.e(tag, ex!!.localizedMessage)
                }

            })
        } catch (e: Exception) {
            Log.e(tag, "Transfer Utility error")
            Log.e(tag, e.localizedMessage)
        }
    }

    private fun getRealPathFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }



}
