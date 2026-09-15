package work.ranjit.quicksmswear

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.wear.compose.material.MaterialTheme
import work.ranjit.quicksmswear.ui.QuickSmsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: QuickSmsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.SEND_SMS] ?: false
        if (smsGranted) {
            viewModel.checkPermission()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AppNavigation(
                    onRequestPermission = {
                        requestPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.SEND_SMS
                            )
                        )
                    },
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPermission()
    }
}
