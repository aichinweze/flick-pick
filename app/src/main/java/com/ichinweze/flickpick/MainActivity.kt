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
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ichinweze.flickpick.repositiories.AccountRepository
import com.ichinweze.flickpick.repositiories.BaselineRepository
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
import com.ichinweze.flickpick.viewmodels.AccountViewModel
import com.ichinweze.flickpick.viewmodels.BaselineViewModel
import com.ichinweze.flickpick.viewmodels.HistoryViewModel
import com.ichinweze.flickpick.viewmodels.LoginViewModel
import com.ichinweze.flickpick.viewmodels.RecommendViewModel

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) DASHBOARD_SCREEN else LOGIN_SCREEN

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                AppNavigation(startDestination)
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
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    val context = LocalContext.current

    val csvRepository: CsvRepositoryImpl = CsvRepositoryImpl(context)

    // Creation Extras for View Models
    val baselineVMCreationExtras = MutableCreationExtras().apply {
        set(BaselineViewModel.CSV_REPOSITORY_KEY, csvRepository)
    }
    val recommendVMCreationExtras = MutableCreationExtras().apply {
        set(RecommendViewModel.CSV_REPOSITORY_KEY, csvRepository)
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
        factory = LoginViewModel.Factory
    )
    val accountViewModel: AccountViewModel = viewModel(
        factory = AccountViewModel.Factory
    )
    val historyViewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.Factory
    )

    NavHost(navController = navController, startDestination = startDestination) {
        composable(LOGIN_SCREEN) {
            LoginScreen(navController, loginViewModel, context)
        }
        composable(route = DASHBOARD_SCREEN) {
            DashboardScreen(navController)
        }
        composable(route = ACCOUNT_INFO_SCREEN) {
            AccountInfoScreen(navController, accountViewModel, context)
        }
        composable(route = HISTORY_SCREEN) {
            HistoryScreen(navController, historyViewModel, context)
        }
        composable(route = BASELINE_Q_SCREEN) {
            BaselineQuestionScreen(navController, baselineViewModel, context)
        }
        composable(route = RECOMMEND_Q_SCREEN) {
            RecommendQuestionScreen(navController, recommendViewModel, context)
        }
    }
}