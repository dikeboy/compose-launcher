package com.lin.comlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.gyf.immersionbar.ImmersionBar
import com.lin.comlauncher.common.PermissionManager
import com.lin.comlauncher.ui.theme.ComposeLauncherTheme
import com.lin.comlauncher.ui.theme.MyBasicColumn
import com.lin.comlauncher.ui.theme.pagerFlingBehavior
import com.lin.comlauncher.util.DisplayUtils
import com.lin.comlauncher.util.LauncherUtils
import com.lin.comlauncher.util.LogUtils
import com.lin.comlauncher.view.DesktopView
import com.lin.comlauncher.view.InitView
import com.lin.comlauncher.view.ToolBarView
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
                // A surface container using the 'background' color from the theme
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
            var height = resources.displayMetrics.heightPixels+ImmersionBar.getStatusBarHeight(this)
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


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
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
    var list = mutableListOf<String>()
    for (i in 0..10)
        list.add("$i")
    var width = LocalConfiguration.current.screenWidthDp
    var height = LocalConfiguration.current.screenHeightDp
    var applist = homeViewModel.appLiveData.observeAsState()

    val state = rememberScrollState()
    var context = LocalContext.current
    ComposeLauncherTheme {
        Scaffold(
            content = {
                Image(painter = painterResource(id = R.drawable.wall_paper),contentDescription="", modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop)
                if(applist.value?.homeList?.size?:0==0){
                    InitView()
                }else{
                    DesktopView(lists = applist,viewModel = homeViewModel)
                    ToolBarView(applist = applist)
                }
            }
        )

    }
}

@Composable
fun TestView(){
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val color = if (isPressed) Color.Blue else Color.Green

    Column(modifier = Modifier.fillMaxHeight().clickable(
        interactionSource = MutableInteractionSource(),indication=null){

    }
        .fillMaxWidth()
        .offset(100.dp,200.dp)
        .background(Color.White)
        .padding(20.dp),
        verticalArrangement= Arrangement.Center) {
            Text(
                "Button",
                color = Color.Black
            )
    }
}
