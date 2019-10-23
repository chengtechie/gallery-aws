package cheng.com.cloudgallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private val tag = "Login_Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        AWSMobileClient.getInstance().initialize(this, object: Callback<UserStateDetails> {
            override fun onResult(result: UserStateDetails?) {
                AWSMobileClient.getInstance().signOut()
                Log.i(tag, "onResult ${result!!.userState}")
            }

            override fun onError(e: Exception?) {
                Log.e(tag, "Initialization error", e)
            }
        })
    }

    fun onLoginClicked(view: View) {
        AWSMobileClient.getInstance().signIn("cheng", "1234567890", null, object: Callback<SignInResult> {
            override fun onResult(result: SignInResult?) {
                runOnUiThread {
                    Log.d(tag, "Sign-in callback state: ${result!!.signInState}")
                    when (result.signInState) {
                        SignInState.DONE -> {
                            Toast.makeText(applicationContext, "Sign-in done", Toast.LENGTH_SHORT).show()
                            val nextActivity = Intent(applicationContext, MainActivity::class.java)
                            startActivity(nextActivity)
                            finish()
                        }
                        SignInState.SMS_MFA -> {
                            Toast.makeText(applicationContext, "Please confirm sign-in with SMS", Toast.LENGTH_SHORT).show()
                        }
                        SignInState.NEW_PASSWORD_REQUIRED -> {
                            Toast.makeText(applicationContext, "Please confirm sign-in with new password", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(applicationContext, "Unsupported sign-in confirmation", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

            override fun onError(e: Exception?) {
                e!!.printStackTrace()
            }

        })
    }

    fun onRegisterClicked(view: View) {
        val signUpActivity = Intent(this, SignUpActivity::class.java)
        startActivity(signUpActivity)
    }
}
