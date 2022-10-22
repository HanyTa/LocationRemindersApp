package com.udacity.project4.authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    @SuppressLint("RestrictedApi")
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        val response = it.idpResponse?.toIntent()
        if (it.resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_LONG).show()
            startRemindersActivity()
        } else {
            Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun startRemindersActivity() {
        startActivity(Intent(this, RemindersActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        if (FirebaseAuth.getInstance().currentUser != null) {
            startRemindersActivity()
        }

        binding.loginButton.setOnClickListener {
            signInLauncher.launch(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                    arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                ).build()
            )
        }

    }
}
