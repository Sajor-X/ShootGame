package sajor.com.shootgame;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import sajor.com.util.Constant;
import sajor.com.view.GameView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.ViewGroup.LayoutParams.*;

public class MainActivity extends Activity {
    // 定义主布局内的容器：FrameLayout
    public static FrameLayout mainLayout = null;
    // 主布局的布局参数
    public static FrameLayout.LayoutParams mainLP = null;
    // 游戏窗口的主游戏界面
    public static GameView mainView = null;

    // 游戏窗口的宽度
    public static int windowWidth;
    // 游戏窗口的高度
    public static int windowHeight;
    // 定义资源管理核心类
    public static Resources res = null;
    public static MainActivity mainActivity = null;
    // 播放背景音乐的MediaPlayer
    private MediaPlayer mediaPlayer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/WaterwaysSeafarers.ttf")
                .setFontAttrId(R.attr.fontPath).build());
        mainActivity = this;
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 获取窗口管理器
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(metrics);

        // 获得屏幕宽和高
        windowWidth = metrics.widthPixels;
        windowHeight = metrics.heightPixels;

        // 加载activity_main.xml界面设计文件
        setContentView(R.layout.activity_main);
        res = getResources();

        // 获取main.xml界面设计文件中ID为mainLayout的组件
        mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
        // 创建GameView组件
        mainView = new GameView(this.getApplicationContext(), Constant.STAGE_INIT);

        mainLP = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        mainLayout.addView(mainView, mainLP);
        // 播放背景音乐
        mediaPlayer = MediaPlayer.create(this, R.raw.game_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
