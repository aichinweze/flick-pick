package com.ichinweze.flickpick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ichinweze.flickpick.screens.AccountInfoScreen
import com.ichinweze.flickpick.screens.BaselineQuestionScreen
import com.ichinweze.flickpick.screens.DashboardScreen
import com.ichinweze.flickpick.screens.HistoryScreen
import com.ichinweze.flickpick.screens.LoginScreen
import com.ichinweze.flickpick.screens.RecommendQuestionScreen
import com.ichinweze.flickpick.screens.utils.ScreenUtils.ACCOUNT_INFO_SCREEN
import com.ichinweze.flickpick.screens.utils.ScreenUtils.BASELINE_Q_SCREEN
import com.ichinweze.flickpick.screens.utils.ScreenUtils.DASHBOARD_SCREEN
import com.ichinweze.flickpick.screens.utils.ScreenUtils.HISTORY_SCREEN
import com.ichinweze.flickpick.screens.utils.ScreenUtils.LOGIN_SCREEN
import com.ichinweze.flickpick.screens.utils.ScreenUtils.RECOMMEND_Q_SCREEN
import com.ichinweze.flickpick.ui.theme.FlickPickTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                AppNavigation()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val navController = rememberNavController()
    //AppNavigation()
    //DashboardScreen(navController)
    //AccountInfoScreen(navController)
    //HistoryScreen(navController)
    BaselineQuestionScreen(navController)
    //RecommendQuestionScreen(navController)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // TODO: Start Destination to dashboard screen if logged in as user

    // View Models


    NavHost(navController = navController, startDestination = LOGIN_SCREEN) {
        composable(LOGIN_SCREEN) {
            LoginScreen(navController)
        }
        composable(route = DASHBOARD_SCREEN) {
            DashboardScreen(navController)
        }
        composable(route = ACCOUNT_INFO_SCREEN) {
            AccountInfoScreen(navController)
        }
        composable(route = HISTORY_SCREEN) {
            HistoryScreen(navController)
        }
        composable(route = BASELINE_Q_SCREEN) {
            BaselineQuestionScreen(navController)
        }
        composable(route = RECOMMEND_Q_SCREEN) {
            RecommendQuestionScreen(navController)
        }
    }
}