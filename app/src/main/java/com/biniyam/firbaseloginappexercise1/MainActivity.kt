package com.biniyam.firbaseloginappexercise1

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.biniyam.firbaseloginappexercise1.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var signInClient: GoogleSignInClient
    lateinit var firebaseAuth: FirebaseAuth


    val signInLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data

            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)
                assert(account != null)
                firebaseAuthWithAccount(account)
            } catch (e: Exception) {
                Toast.makeText(this, "failed to fetch Google Account", Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.WEB_CLIENT_ID))
            .requestEmail()
            .build()

        signInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val intent = signInClient.signInIntent

            signInLauncher.launch(intent)

        }

    }

    private fun firebaseAuthWithAccount(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = firebaseAuth.currentUser

                val uid = user?.uid
                val email = user?.email
                val name = user?.displayName

                Log.w(TAG, "user id: $uid")
                Log.w(TAG, "user email: $email")
                Log.w(TAG, "user name: $name")

                binding.tvName.text = "hello $name with id $uid"

            }
            .addOnFailureListener {
                Log.w(TAG, "failed to authenticate firebase")
                Toast.makeText(
                    this,
                    "failed to authenticate firebase Google Account",
                    Toast.LENGTH_LONG
                ).show()

            }

    }
}