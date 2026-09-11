package work.ranjit.quicksmswear.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import work.ranjit.quicksmswear.data.Contact
import work.ranjit.quicksmswear.ui.MainSmsScreen
import work.ranjit.quicksmswear.ui.QuickSmsUiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainSmsScreenTest {

    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sampleContacts = listOf(
        Contact(name = "Test Contact", phoneNumber = "123456", defaultMessage = "Test message")
    )

    @Before
    fun setup() {
        composeTestRule.setContent {
            MainSmsScreen(
                uiState = QuickSmsUiState(
                    contacts = sampleContacts,
                    hasPermission = true
                ),
                onRequestPermission = {},
                onContactClick = {},
                onContactLongClick = {},
                onManageContactsClick = {}
            )
        }
    }

    @Test
    fun contact_exists_on_screen() {
        composeTestRule.onNodeWithText("Test Contact").assertExists()
    }
}
