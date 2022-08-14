package com.lin.comlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import com.gyf.immersionbar.ImmersionBar
import com.lin.comlauncher.common.PermissionManager
import com.lin.comlauncher.ui.theme.ComposeLauncherTheme
import com.lin.comlauncher.util.DisplayUtils
import com.lin.comlauncher.util.LauncherConfig
import com.lin.comlauncher.util.LauncherUtils
import com.lin.comlauncher.util.LogUtils
import com.lin.comlauncher.view.DesktopView
import com.lin.comlauncher.view.InitView
import com.lin.comlauncher.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private var isLoadApp = false;
    var permissionManager = PermissionManager(101)
    var startTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImmersionBar.with(this).transparentStatusBar().init();
        initView()
        setContent {
            ComposeLauncherTheme {
                Surface(color = MaterialTheme.colors.background) {
//                    SwipeableSample(homeViewModel)
                    var height = DisplayUtils.getScreenHeightCanUse(this)+ImmersionBar.getStatusBarHeight(this)
                    LocalConfiguration.current.screenHeightDp = DisplayUtils.pxToDp(height)

                    createView(homeViewModel){

                    }
//                    TestView()
                }
            }
        }
    }
    fun initView(){
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        var arrayPermission =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                 arrayOf(Manifest.permission.QUERY_ALL_PACKAGES,Manifest.permission.VIBRATE)
            else arrayOf(Manifest.permission.VIBRATE)
        permissionManager.checkPermission(this,arrayPermission){
            var width = resources.displayMetrics.widthPixels
            var height = LauncherUtils.getScreenHeight3(this)
//            var height = resources.displayMetrics.heightPixels+ImmersionBar.getStatusBarHeight(this)
            homeViewModel.loadApp(packageManager,width =width,height = height)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

@Preview(showBackground = true, backgroundColor = 0x40000000)
@Composable
fun DefaultPreview() {
    createView(homeViewModel = HomeViewModel()) {
        Log.e("linlog","test")
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun createView(homeViewModel: HomeViewModel,onClick: () -> Unit) {
    var width = LocalConfiguration.current.screenWidthDp
    var height = LocalConfiguration.current.screenHeightDp
    LauncherConfig.HOME_WIDTH = width;
    LauncherConfig.HOME_HEIGHT = height;
    var versionLiveState = homeViewModel.appVersionLiveData.observeAsState()
    var applist = homeViewModel.infoBaseBean

    LogUtils.e("recreate ${versionLiveState.value} ")

    ComposeLauncherTheme {
        Scaffold(
            content = { padding ->
                Image(painter = painterResource(id = R.drawable.wall_paper),contentDescription="", modifier = Modifier.padding(padding)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop)

//                Image(
//                    painter = rememberAsyncImagePainter( R.drawable.wall_paper),
//                    contentDescription = null,
//                    modifier = Modifier.padding(padding)
//                            .fillMaxHeight()
//                            .fillMaxWidth(),
//                    contentScale = ContentScale.Crop
//                )
                LogUtils.e("drag")
                var version = versionLiveState.value
                if(applist.homeList?.size?:0==0){
                    InitView(applist)
                }else{
                    DesktopView(lists = applist,viewModel = homeViewModel)
                }
            }
        )

    }
}

@Composable
fun TestView(){
    LogUtils.e("change 2");
    Column() {
        LogUtils.e("change 0");
        Box {
            LogUtils.e("change 1");
            var dragState = remember { mutableStateOf(0.dp) }

            Column(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .offset(100.dp, 200.dp)
                .background(Color.White)
                .padding(20.dp),
                verticalArrangement= Arrangement.Center) {
                Text(
                    "Button ${dragState.value}",
                    color = Color.Black
                )
            }
            TestButtonView(dragState)

        }

    }
}

@Composable
fun TestButtonView(dragState: MutableState<Dp>) {
    Button(onClick = {
        dragState.value =100.dp
        LogUtils.e("dp = $dragState")
    },
        Modifier
            .offset(100.dp, 200.dp)
            .size(100.dp, 100.dp)) {
        Text(text = "Click Me")
    }
}
