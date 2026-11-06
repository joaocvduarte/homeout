package app.homeout

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AppTest {
    @Test
    fun appRendersItsRoot() = runComposeUiTest {
        setContent { App() }
        onNodeWithTag("app-root").assertIsDisplayed()
    }
}
