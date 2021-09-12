package com.lin.comlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
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
import com.lin.comlauncher.util.LauncherUtils
import com.lin.comlauncher.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private var isLoadApp = false;
    var permissionManager = PermissionManager(101)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).transparentStatusBar().init();
        initView()
        setContent {
            ComposeLauncherTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
//                    SwipeableSample(homeViewModel)
                    createView(homeViewModel){

                    }
                }
            }
        }
    }
    fun initView(){
        permissionManager.checkPermission(this,arrayOf(Manifest.permission.QUERY_ALL_PACKAGES)){
            var width = resources.displayMetrics.widthPixels
            var height = resources.displayMetrics.heightPixels
            homeViewModel.loadApp(packageManager,width =width,height = height)
        }
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
        Log.e("lin","test")
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
                if(applist.value?.size?:0==0){
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                        Text(text = "初始化中...")
                    }
                }else{
                    Row(modifier = Modifier
                        .width(width = width.dp)
                        .height(height = height.dp)
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(
                            state,
                            flingBehavior = pagerFlingBehavior(
                                state,
                                (applist.value?.size ?: 0)
                            )
                        )
                    ) {
                        applist.value?.forEachIndexed { index, list ->
                            Column(modifier = Modifier
                                .width(width = width.dp)
                                .height(height = height.dp)) {
                                MyBasicColumn(){
                                    list.forEach {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .size(it.width.dp, it.height.dp)
                                                .offset(it.posX.dp, it.posY.dp)
                                                .clickable {
                                                    LauncherUtils.startApp(context,it )
                                                }) {
                                            it.icon?.let { icon->
                                                Image(icon.asImageBitmap(), contentDescription = "",
                                                    modifier = Modifier.size(52.dp,52.dp))

                                            }
                                            Text(text = it.name?:"",overflow = TextOverflow.Ellipsis,maxLines = 1,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(4.dp,10.dp,4.dp,0.dp))
                                        }

                                    }
                                }
                            }
                        }
                    }
                }

            }
        )

    }
}

@Composable
fun ChildView(message:String,onClick: () -> Unit) {
    var resource = painterResource(id = R.drawable.aaa)
    var padding = Dp(16f)
    Card(
        elevation = 4.dp, backgroundColor = Color.White,
        modifier = Modifier.clip(shape = RoundedCornerShape(10.dp))
    ) {
        Row(
            Modifier
                .size(Dp.Unspecified, 140.dp)
                .clickable(onClick = onClick)
                .padding(padding)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.aaa),
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .clip(CircleShape),
                contentDescription = "test",
                contentScale = ContentScale.Crop
            )
            Column(Modifier.offset(x = 10.dp)) {
                Text(text = "hello $message")
            }

        }
    }
}
