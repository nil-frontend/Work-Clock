package com.example.workclock.ads

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

private const val TAG = "BannerAdHost"
// Official Google Mobile Ads sample banner ad unit ID for testing
private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"

@Composable
fun BannerAdHost(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF0D1118)
) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp

    // If available height is constrained (e.g. short landscape on phones), suppress the banner
    // to preserve clock space per UI-UX specifications
    if (screenHeightDp < 320) {
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Check whether Google Play Services is available on this device/emulator
    val isPlayServicesAvailable = remember(context) {
        try {
            val availability = GoogleApiAvailability.getInstance()
            availability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        } catch (e: Throwable) {
            Log.w(TAG, "Unable to verify Google Play Services availability: ${e.message}")
            false
        }
    }

    // If Play Services is not available (e.g. headless/streaming emulator or AOSP build),
    // display a clean fallback banner without attempting IPC binder calls that fail
    if (!isPlayServicesAvailable) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(backgroundColor)
                .padding(vertical = 4.dp)
                .testTag("banner_ad_container"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "WorkClock • Test Mode",
                color = Color.Gray.copy(alpha = 0.5f),
                fontSize = 11.sp,
                modifier = Modifier.testTag("ad_fallback_placeholder")
            )
        }
        return
    }

    var adLoaded by remember { mutableStateOf(false) }
    var adFailed by remember { mutableStateOf(false) }

    // Initialize MobileAds asynchronously
    LaunchedEffect(Unit) {
        try {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "MobileAds initialized: $status")
            }
        } catch (e: Throwable) {
            Log.w(TAG, "MobileAds initialization failed gracefully: ${e.message}")
            adFailed = true
        }
    }

    val adView = remember {
        try {
            AdView(context).apply {
                adUnitId = TEST_BANNER_AD_UNIT_ID
                setAdSize(AdSize.BANNER)
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        adLoaded = true
                        adFailed = false
                        Log.d(TAG, "Banner ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        adLoaded = false
                        adFailed = true
                        Log.w(TAG, "Banner ad failed to load: ${error.message} (Code: ${error.code})")
                    }
                }
                post {
                    try {
                        loadAd(AdRequest.Builder().build())
                    } catch (e: Throwable) {
                        Log.w(TAG, "Exception requesting banner ad: ${e.message}")
                        adFailed = true
                    }
                }
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Failed creating AdView: ${e.message}")
            adFailed = true
            null
        }
    }

    if (adView != null) {
        DisposableEffect(lifecycleOwner, adView) {
            val observer = LifecycleEventObserver { _, event ->
                try {
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> adView.resume()
                        Lifecycle.Event.ON_PAUSE -> adView.pause()
                        Lifecycle.Event.ON_DESTROY -> adView.destroy()
                        else -> Unit
                    }
                } catch (e: Throwable) {
                    Log.w(TAG, "Error updating AdView lifecycle: ${e.message}")
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                try {
                    adView.destroy()
                } catch (e: Throwable) {
                    Log.w(TAG, "Error destroying AdView: ${e.message}")
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(backgroundColor)
            .padding(vertical = 4.dp)
            .testTag("banner_ad_container"),
        contentAlignment = Alignment.Center
    ) {
        if (adView != null && !adFailed) {
            AndroidView(
                factory = { adView },
                modifier = Modifier.testTag("admob_banner_view")
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("ad_fallback_placeholder"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Advertisement (Test Mode)",
                    color = Color.Gray.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
