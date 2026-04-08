package navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kizakuu.LoginScreen
import com.example.kizakuu.RegisterScreen
import com.example.kizakuu.WelcomeScreen
import dashboard.DashboardScreen
import splashscreen.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_LOGIN
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(ROUTE_DASHBOARD) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(ROUTE_REGISTER)
                }
            )
        }
        composable(ROUTE_REGISTER) {
            RegisterScreen(
                onLoginClick = {
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_REGISTER) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_REGISTER) { inclusive = true }
                    }
                }
            )
        }
        composable(ROUTE_DASHBOARD) {
            DashboardScreen(navController)
        }
        composable(ROUTE_SPLASH) {
            SplashScreen(onTimeout = {
                navController.navigate(ROUTE_LOGIN) {
                    popUpTo(ROUTE_SPLASH) { inclusive = true }
                }
            })
        }
        composable(ROUTE_MAIN) {
            WelcomeScreen(
                onLoginClick = {
                    navController.navigate(ROUTE_LOGIN)
                },
                onRegisterClick = {
                    navController.navigate(ROUTE_REGISTER)
                }
            )
        }
    }
}
