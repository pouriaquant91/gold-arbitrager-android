package com.pouriaquant.goldarb

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pouriaquant.goldarb.security.AppPreferences
import com.pouriaquant.goldarb.security.AppThemeMode
import com.pouriaquant.goldarb.security.AppVisualStyle
import com.pouriaquant.goldarb.ui.GoldArbApp
import com.pouriaquant.goldarb.ui.LockScreen
import com.pouriaquant.goldarb.ui.theme.RasadTheme
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    private val preferences by lazy { AppPreferences(applicationContext) }
    private var unlocked by mutableStateOf(true)
    private var biometricEnabled by mutableStateOf(false)
    private var themeMode by mutableStateOf(AppThemeMode.SYSTEM)
    private var visualStyle by mutableStateOf(AppVisualStyle.GRAPHITE)
    private var authenticationRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        biometricEnabled = preferences.biometricLockEnabled && biometricAvailable()
        if (preferences.biometricLockEnabled && !biometricEnabled) preferences.biometricLockEnabled = false
        lifecycleScope.launch {
            preferences.themeModeFlow.collect { savedMode -> themeMode = savedMode }
        }
        lifecycleScope.launch {
            preferences.visualStyleFlow.collect { savedStyle -> visualStyle = savedStyle }
        }
        unlocked = !biometricEnabled
        setContent {
            val darkTheme = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
            RasadTheme(darkTheme = darkTheme, visualStyle = visualStyle) {
                if (biometricEnabled && !unlocked) {
                    LockScreen(biometricAvailable = biometricAvailable(), onUnlock = ::authenticate)
                } else {
                    GoldArbApp(
                        biometricAvailable = biometricAvailable(),
                        biometricEnabled = biometricEnabled,
                        themeMode = themeMode,
                        visualStyle = visualStyle,
                        onBiometricChanged = ::requestBiometricSetting,
                        onThemeModeChanged = { mode ->
                            themeMode = mode
                            lifecycleScope.launch { preferences.setThemeMode(mode) }
                        },
                        onVisualStyleChanged = { style ->
                            visualStyle = style
                            lifecycleScope.launch { preferences.setVisualStyle(style) }
                        },
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (biometricEnabled && !unlocked) authenticate()
    }

    override fun onStop() {
        if (biometricEnabled && !isChangingConfigurations) unlocked = false
        super.onStop()
    }

    private fun biometricAvailable(): Boolean =
        BiometricManager.from(this).canAuthenticate(authenticators()) == BiometricManager.BIOMETRIC_SUCCESS

    private fun requestBiometricSetting(enabled: Boolean) {
        if (!enabled) {
            preferences.biometricLockEnabled = false
            biometricEnabled = false
            unlocked = true
            return
        }
        authenticate {
            preferences.biometricLockEnabled = true
            biometricEnabled = true
            unlocked = true
        }
    }

    private fun authenticate(onSuccess: () -> Unit = { unlocked = true }) {
        if (authenticationRunning || !biometricAvailable()) return
        authenticationRunning = true
        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    authenticationRunning = false
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    authenticationRunning = false
                }
            },
        )
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("ورود امن به رصد")
            .setSubtitle("با اثر انگشت، چهره یا قفل دستگاه وارد شوید")
            .setAllowedAuthenticators(authenticators())
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) builder.setNegativeButtonText("انصراف")
        prompt.authenticate(builder.build())
    }

    private fun authenticators(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    } else {
        BiometricManager.Authenticators.BIOMETRIC_WEAK
    }
}
