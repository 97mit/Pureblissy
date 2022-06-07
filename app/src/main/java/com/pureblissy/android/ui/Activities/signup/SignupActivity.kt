package com.pureblissy.android.ui.Activities.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.pureblissy.android.R
import com.pureblissy.android.databinding.ActivityLoginBinding
import com.pureblissy.android.databinding.ActivitySignupBinding
import com.pureblissy.android.ui.Activities.login.LoginActivity
import com.stimednp.roommvvm.utils.UtilExtensions.makeLinks
import com.stimednp.roommvvm.utils.UtilExtensions.openActivity

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*handle click on texts*/
        binding.tvAlreadyHaveAccount.makeLinks(
            Pair("Login", View.OnClickListener {
                openActivity(LoginActivity::class.java)
            }))
        binding.tvPolicy.makeLinks(
            Pair("Term & Conditions", View.OnClickListener {
                Toast.makeText(applicationContext, "Terms of Service Clicked", Toast.LENGTH_SHORT).show()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Toast.makeText(applicationContext, "Privacy Policy Clicked", Toast.LENGTH_SHORT).show()
            })
        )
    }
}