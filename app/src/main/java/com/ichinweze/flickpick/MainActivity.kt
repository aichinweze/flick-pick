package com.ichinweze.flickpick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ichinweze.flickpick.repositiories.CsvRepositoryImpl
import com.ichinweze.flickpick.repositiories.LoginRepository
import com.ichinweze.flickpick.screens.AccountInfoScreen
import com.ichinweze.flickpick.screens.BaselineQuestionScreen
import com.ichinweze.flickpick.screens.DashboardScreen
import com.ichinweze.flickpick.screens.HistoryScreen
import com.ichinweze.flickpick.screens.LoginScreen
import com.ichinweze.flickpick.screens.RecommendQuestionScreen
import com.ichinweze.flickpick.screens.utils.ACCOUNT_INFO_SCREEN
import com.ichinweze.flickpick.screens.utils.BASELINE_Q_SCREEN
import com.ichinweze.flickpick.screens.utils.DASHBOARD_SCREEN
import com.ichinweze.flickpick.screens.utils.HISTORY_SCREEN
import com.ichinweze.flickpick.screens.utils.LOGIN_SCREEN
import com.ichinweze.flickpick.screens.utils.RECOMMEND_Q_SCREEN
import com.ichinweze.flickpick.viewmodels.BaselineViewModel
import com.ichinweze.flickpick.viewmodels.LoginViewModel
import com.ichinweze.flickpick.viewmodels.RecommendViewModel

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
    //BaselineQuestionScreen(navController)
    //RecommendQuestionScreen(navController)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // TODO: Start Destination to dashboard screen if logged in as user

    val context = LocalContext.current

    val csvRepository: CsvRepositoryImpl = CsvRepositoryImpl(context)
    val loginRepository: LoginRepository = LoginRepository(context)

    // Creation Extras for View Models
    val baselineVMCreationExtras = MutableCreationExtras().apply {
        set(BaselineViewModel.CSV_REPOSITORY_KEY, csvRepository)
    }
    val recommendVMCreationExtras = MutableCreationExtras().apply {
        set(RecommendViewModel.CSV_REPOSITORY_KEY, csvRepository)
    }
    val loginVMCreationExtras = MutableCreationExtras().apply {
        set(LoginViewModel.LOGIN_REPOSITORY_KEY, loginRepository)
    }

    // View Models
    val baselineViewModel: BaselineViewModel = viewModel(
        factory = BaselineViewModel.Factory,
        extras = baselineVMCreationExtras
    )
    val recommendViewModel: RecommendViewModel = viewModel(
        factory = RecommendViewModel.Factory,
        extras = recommendVMCreationExtras
    )
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory,
        extras = loginVMCreationExtras
    )

    NavHost(navController = navController, startDestination = LOGIN_SCREEN) {
        composable(LOGIN_SCREEN) {
            LoginScreen(navController, loginViewModel, context)
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
            BaselineQuestionScreen(navController, baselineViewModel, context)
        }
        composable(route = RECOMMEND_Q_SCREEN) {
            RecommendQuestionScreen(navController, recommendViewModel, context)
        }
    }
}