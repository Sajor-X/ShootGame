package sajor.com.shootgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sajor.com.shootgame.comp.Player;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    public static final Player playerHero = new Player();
    // 保存当前Android应用的主Context
    private Context mainContext = null;

    // 画图所需要的Paint和Canvas对象
    private Paint paint = null;
    private Canvas canvas = null;

    // SurfaceHolder负责维护SurfaceView上绘制的内容
    private SurfaceHolder surfaceHolder;

    // 定义该游戏当前处于何种场景的变量
    private int gStage = 0;

    // 定义一个集合来保存该游戏已经加载到所有场景
    public static final List<Integer> stageList =
            Collections.synchronizedList(new ArrayList<Integer>());

    public GameView(Context context, int firstStage) {
        super(context);
        mainContext = context;
        paint = new Paint();
        // 设置抗锯齿
        paint.setAntiAlias(true);

        // 设置该组件会保持屏幕常量，避免游戏过程中出现黑屏。
        setKeepScreenOn(true);

        // 设置焦点，相应事件处理
        setFocusable(true);

        // 获取SurfaceHolder
        surfaceHolder = getHolder();

        // 设置this为SurfaceHolder的回调，这要求该类实现SurfaceHolder.Callback接口
        surfaceHolder.addCallback(this);

        //初始化屏幕大小
        ViewManager.initScreen(MainActivity.windowWidth, MainActivity.windowHeight);
        gStage = firstStage;
    }

    // 处理游戏场景
    public int doStage(int stage, int step)
    {
        int nextStage;
        switch (stage)
        {
            case Constant.STAGE_INIT:
                nextStage = doInit(step);
                break;
            case Constant.STAGE_LOGIN:
                nextStage = doLogin(step);
                break;
            case Constant.STAGE_GAME:
                nextStage = doGame(step);
                break;
            case Constant.STAGE_LOSE:
                nextStage = doLose(step);
                break;
            default:
                nextStage = Constant.STAGE_ERROR;
                break;
        }
        return nextStage;
    }

    public void stageLogic()
    {
        int newStage = doStage(gStage, Constant.LOGIC);
        if (newStage != Constant.STAGE_NO_CHANGE && newStage != gStage)
        {
            doStage(gStage, Constant.CLEAN); // 清除旧的场景
            gStage = newStage & 0xFF;
            doStage(gStage, Constant.INIT);
        }
        else if (stageList.size() > 0)
        {
            newStage = Constant.STAGE_NO_CHANGE;
            synchronized (stageList)
            {
                newStage = stageList.get(0);
                stageList.remove(0);
            }
            if (newStage == Constant.STAGE_NO_CHANGE)
            {
                return;
            }
            doStage(gStage, Constant.CLEAN); // 清楚旧的场景
            gStage = newStage & 0xFF;
            doStage(gStage, Constant.INIT);
        }
    }

    public int doInit(int step){
        ViewManager.loadResource();
        //return Constant.STAGE_LOGIN;
        return Constant.STAGE_GAME;
    }

    public int doLogin(int step){
        return Constant.STAGE_LOGIN;
    }

    public int doLose(int step){
        return Constant.STAGE_LOSE;
    }

    public Handler setViewHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            RelativeLayout layout = (RelativeLayout) msg.obj;
            if (layout != null) {
                RelativeLayout.LayoutParams params = new RelativeLayout
                        .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                MainActivity.mainLayout.addView(layout, params);
            }
        }
    };
    public Handler delViewHandler = new Handler()
    {
        public void handleMessage(Message msg) {
            RelativeLayout layout = (RelativeLayout) msg.obj;
            if (layout != null) {
                MainActivity.mainLayout.removeView(layout);
            }
        }
    };

    // 定义游戏界面
    RelativeLayout gameLayout = null;
    public int doGame(int step){
        switch(step){
            case Constant.INIT:
                // 初始化游戏界面
                if (gameLayout == null) {
                    gameLayout = new RelativeLayout(mainContext);

                }
                break;
            case Constant.LOGIC:
                // 随机生成怪物

                // 检查碰撞

                // 角色跳与移动

                // 角色死亡
//                if (player.isDie())
//                {
//                    stageList.add(STAGE_LOSE);
//                }
                break;
            case Constant.CLEAN:
                // 清除游戏界面
                if (gameLayout != null)
                {
                    delViewHandler.sendMessage(delViewHandler.obtainMessage(0, gameLayout));
                    gameLayout = null;
                }
                break;
            case Constant.PAINT:
                // 画游戏元素
                ViewManager.clearScreen(canvas);
                ViewManager.drawGame(canvas);
                break;
        }
        return Constant.STAGE_NO_CHANGE;
    }

    class GameThread extends Thread{
        public SurfaceHolder surfaceHolder = null;
        public boolean needStop = false;
        public GameThread(SurfaceHolder holder) {
            this.surfaceHolder = holder;
        }

        @Override
        public void run() {
            super.run();
            long t1, t2;
            Looper.prepare();
            synchronized (surfaceHolder) {
                // 游戏未退出
                while (gStage != Constant.STAGE_QUIT && needStop == false) {
                    try {
                        // 处理游戏的场景逻辑
                        stageLogic();
                        t1 = System.currentTimeMillis();
                        canvas = surfaceHolder.lockCanvas();
                        if (canvas != null)  {
                            // 处理游戏场景
                            doStage(gStage, Constant.PAINT);
                        }
                        t2 = System.currentTimeMillis();
                        int paintTime = (int) (t2 - t1);
                        long millis = Constant.SLEEP_TIME - paintTime;
                        if (millis < Constant.MIN_SLEEP) {
                            millis = Constant.MIN_SLEEP;
                        }
                        // 该线程暂停millis毫秒后再次调用doStage()方法
                        sleep(millis);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            if (canvas != null) {
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Looper.loop();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 游戏线程
    private GameThread thread = null;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 启动主线程执行部分
        paint.setTextSize(15);
        if (thread != null) {
            thread.needStop = true;
        }
        thread = new GameThread(surfaceHolder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
