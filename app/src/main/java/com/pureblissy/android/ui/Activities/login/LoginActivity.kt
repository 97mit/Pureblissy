package com.pureblissy.android.ui.Activities.login

import android.content.ContentValues.TAG
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.gson.Gson
import com.pureblissy.android.R
import com.pureblissy.android.databinding.ActivityLoginBinding
import com.pureblissy.android.ui.Activities.signup.SignupActivity
import com.stimednp.roommvvm.utils.UtilExtensions.changeColor
import com.stimednp.roommvvm.utils.UtilExtensions.makeLinks
import com.stimednp.roommvvm.utils.UtilExtensions.openActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import java.util.*


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private val REQUEST_CODE_GOOGLE_SIGN_IN = 1 /* unique request id */
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val EMAIL = "email"
    private lateinit var callbackManager: CallbackManager
    private lateinit var fbloginButton: LoginButton
    private val RC_SIGN_IN = 9001

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.etUsername
        val password = binding.etPassword
        val login = binding.login
        val loading = binding.loading


        googleSignIn()
        facebookSignIn()
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }

        /*handle click on texts*/
        binding.tvSignup.makeLinks(
            Pair("Sign Up", View.OnClickListener {
                openActivity(SignupActivity::class.java)
            }))
        binding.tvSignup.changeColor("or continue with",R.color.gray_100)
        binding.tvPolicy.makeLinks(
            Pair("Term & Conditions", View.OnClickListener {
                Toast.makeText(applicationContext, "Terms of Service Clicked", Toast.LENGTH_SHORT).show()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Toast.makeText(applicationContext, "Privacy Policy Clicked", Toast.LENGTH_SHORT).show()
            })
        )

    }
    fun onCustomFbClick(v: View) {
        if (v === binding.btCustomFbLogin) {
            binding.btFacebookSignIn.performClick()
        }
    }
    fun onCustomGoogleClick(v: View) {
        if (v === binding.btCustomGoogleLogin) {
            binding.btGoogleSignIn.performClick()
        }
    }
    fun facebookSignIn() {
        fbloginButton = binding.btFacebookSignIn
        callbackManager = CallbackManager.Factory.create()
        fbloginButton.setPermissions(listOf("email", "user_birthday"))

        //login callback
        fbloginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                val userId = loginResult?.accessToken?.userId
                Log.d(TAG, "onSuccess: userId $userId")
                Log.d(TAG, "onSuccess: userId ===${Gson().toJson(loginResult)}")

                val bundle = Bundle()
                bundle.putString("fields", "id, email, first_name, last_name, gender,age_range")


                //Graph API to access the data of user's facebook account
                val request = GraphRequest.newMeRequest(
                    loginResult?.accessToken
                ) { fbObject, response ->
                    Log.v("Login Success", response.toString())


                    //For safety measure enclose the request with try and catch
                    try {

                        Log.d(TAG, "onSuccess: fbObject $fbObject")

                        val firstName = fbObject?.getString("first_name")
                        val lastName = fbObject?.getString("last_name")
                        val gender = fbObject?.getString("gender")
                        val email = fbObject?.getString("email")

                        Log.d(TAG, "onSuccess: firstName $firstName")
                        Log.d(TAG, "onSuccess: lastName $lastName")
                        Log.d(TAG, "onSuccess: gender $gender")
                        Log.d(TAG, "onSuccess: email $email")

                    } //If no data has been retrieve throw some error
                    catch (e: JSONException) {

                    }

                }
                //Set the bundle's data as Graph's object data
                request.parameters = bundle

                //Execute this Graph request asynchronously
                request.executeAsync()

            }

            override fun onCancel() {
                Log.d(TAG, "onCancel: called")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "onError: called")
            }
        })

    }


    fun googleSignIn(){
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.server_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
        binding.btGoogleSignIn.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        intentSenderRequestLauncher.launch(intentSenderRequest)
                        /*startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)*/
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(TAG, e.localizedMessage)
                }
        }
    }

    private var startForResult= registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d(TAG, "Got ID token."+idToken)
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d(TAG, "Got password."+password)
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(TAG, "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d(TAG, "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                        }
                    }
                }
            }
        }
    }*/
    private val intentSenderRequestLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result != null) {
                val intent = result.data
                when (result.resultCode) {
                    RESULT_OK -> {
                        try {
                            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                            val idToken = credential.googleIdToken
                            val username = credential.id
                            val password = credential.password
                            Log.d("fdssd======", Gson().toJson(credential).toString())
                            when {
                                idToken != null -> {
                                    // Got an ID token from Google. Use it to authenticate
                                    // with your backend.
                                    Log.d(TAG, "Got ID token."+idToken)
                                }
                                password != null -> {
                                    // Got a saved username and password. Use them to authenticate
                                    // with your backend.
                                    Log.d(TAG, "Got password."+password)
                                }
                                else -> {
                                    // Shouldn't happen.
                                    Log.d(TAG, "No ID token or password!")
                                }
                            }
                        } catch (e: ApiException) {
                            when (e.statusCode) {
                                CommonStatusCodes.CANCELED -> {
                                    Log.d(TAG, "One-tap dialog was closed.")
                                    // Don't re-prompt the user.
                                    showOneTapUI = false
                                }
                                CommonStatusCodes.NETWORK_ERROR -> {
                                    Log.d(TAG, "One-tap encountered a network error.")
                                    // Try again or just ignore.
                                }
                                else -> {
                                    Log.d(TAG, "Couldn't get credential from result." +
                                            " (${e.localizedMessage})")
                                }
                            }
                        }
                    }
                }
            }
        }
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}