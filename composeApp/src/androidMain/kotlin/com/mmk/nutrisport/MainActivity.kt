package com.mmk.nutrisport

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.nutrisport.shared.util.PreferenceUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.android.ext.android.inject
import kotlin.coroutines.resumeWithException

class MainActivity : ComponentActivity() {

    val preferenceUtils by inject<PreferenceUtils>()

    val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            account?.let { onSignInAccountAvailable(this, it) } // callback to ViewModel
        } catch (e: Exception) {
            // handle error
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onSignInAccountAvailable(context: Context, account: GoogleSignInAccount) {
        GlobalScope.launch {
            val cachedToken = preferenceUtils.getGoogleToken()
            if (cachedToken != null) {
                runOnUiThread {
                    Toast.makeText(context, "The token is = $cachedToken", Toast.LENGTH_LONG).show()
                }
            } else {
                // Fetch a new token
                onSignInAccountAvailable(context, account)
            }
        }
    }

    // Launch interactive sign-in
    fun launchGoogleSignIn(activity: MainActivity, signInLauncher: ActivityResultLauncher<Intent>) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("https://www.googleapis.com/auth/drive.file"))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(activity, gso)
        signInLauncher.launch(client.signInIntent)
    }

    suspend fun getTokenFromAccount(context: Context, account: GoogleSignInAccount?): String =
        suspendCancellableCoroutine { cont ->
            Thread {
                try {
                    // Scope for per-file Drive access
                    val scope = "oauth2:https://www.googleapis.com/auth/drive.file"

                    // Fetch short-lived access token
                    val token = GoogleAuthUtil.getToken(context, account?.account!!, scope)

                    cont.resume(
                        token,
                        onCancellation = null
                    )
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            }.start()
        }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }

        // Check if we already have a cached token
        var cachedToken: String? = ""
        lifecycleScope.launch {
            cachedToken = preferenceUtils.getGoogleToken()
        }
        if (cachedToken != "") {
            Toast.makeText(this, "Using cached token: $cachedToken", Toast.LENGTH_LONG).show()
            return
        }

        // Check if there is a previously signed-in account
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (lastSignedInAccount != null) {
            // Use existing account to fetch token
            lifecycleScope.launch {
                try {
                    val token = getTokenFromAccount(this@MainActivity, lastSignedInAccount)
                    preferenceUtils.setGoogleToken(token)
                    Toast.makeText(this@MainActivity, "Fetched token: $token", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Token fetch failed: ${e.message}", Toast.LENGTH_LONG).show()
                    launchGoogleSignIn(this@MainActivity, signInLauncher)
                }
            }
        } else {
            // No account, start interactive sign-in
            launchGoogleSignIn(this, signInLauncher)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}