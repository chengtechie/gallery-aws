package cheng.com.cloudgallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.results.SignUpResult
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {

    private val tag = "Sign_Up_Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        AWSMobileClient.getInstance().initialize(this, object: Callback<UserStateDetails> {
            override fun onResult(result: UserStateDetails?) {
                Log.i(tag, "onResult ${result!!.userState}")
            }

            override fun onError(e: Exception?) {
                Log.e(tag, "Initialization error", e)
            }
        })
    }

    fun onRegisterClicked(view: View) {
//        val usn = username.text.toString()
//        val pw = password.text.toString()
        val attributes = HashMap<String, String>()
//        attributes["email"] = email.text.toString()
        attributes["email"] = "chengchin5227@gmail.com"
        AWSMobileClient.getInstance().signUp("cheng", "1234567890", attributes, null, object: Callback<SignUpResult> {
            override fun onResult(result: SignUpResult?) {
                runOnUiThread {
                    Log.d(tag, "Sign -up callback state ${result!!.confirmationState}")
                    if (!result.confirmationState) {
                        val details = result.userCodeDeliveryDetails
                        Toast.makeText(applicationContext, "Confirm sign-up with: ${details.destination}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Sign-up done", Toast.LENGTH_SHORT).show()
                        finish()
                        //arn:aws:s3:::cloudgallerys3storage-cloud
                    }
                }
            }

            override fun onError(e: Exception?) {
                e!!.printStackTrace()
            }
        })
    }
}
