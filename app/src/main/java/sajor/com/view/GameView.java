package sajor.com.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sajor.com.shootgame.EnemyManager;
import sajor.com.shootgame.MainActivity;
import sajor.com.object.Player;
import sajor.com.shootgame.R;
import sajor.com.util.Constant;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    public static int allCount=0;
    public static final Player playerHero = new Player();

    // 保存当前Android应用的主Context
    private Context mainContext;
    // 判断玩家是否按下屏幕
    private boolean isTouchPlane;
    // 画图所需要的Paint和Canvas对象
    private Paint paint;
    private Canvas canvas = null;
    // SurfaceHolder负责维护SurfaceView上绘制的内容
    private SurfaceHolder surfaceHolder;

    // 定义该游戏当前处于何种场景的变量
    private int gStage;

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
    public int doStage(int stage, int step) {
        int nextStage;
        switch (stage) {
            case Constant.STAGE_INIT:
                nextStage = doInit();
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

    public void stageLogic() {
        int newStage = doStage(gStage, Constant.LOGIC);
        if (newStage != Constant.STAGE_NO_CHANGE && newStage != gStage) {
            doStage(gStage, Constant.CLEAN); // 清除旧的场景
            gStage = newStage & 0xFF;
            doStage(gStage, Constant.INIT);
        }
        else if (stageList.size() > 0) {
            // newStage = Constant.STAGE_NO_CHANGE;
            synchronized (stageList) {
                newStage = stageList.get(0);
                stageList.remove(0);
            }
            if (newStage == Constant.STAGE_NO_CHANGE) {
                return;
            }
            doStage(gStage, Constant.CLEAN); // 清楚旧的场景
            gStage = newStage & 0xFF;
            doStage(gStage, Constant.INIT);
        }
    }

    public int doInit(){
        ViewManager.loadResource();
        playerHero.setDie(false);
        playerHero.setScore(0);
        EnemyManager.enemyList.clear();
        return Constant.STAGE_LOGIN;
    }

    // 定义登录界面
    private RelativeLayout loginView;
    public int doLogin(int step){
        switch (step) {
            case Constant.INIT:
                // 初始化角色分数
                playerHero.setScore(0);

                // 初始化登录界面
                if (loginView == null) {
                    loginView = new RelativeLayout(mainContext);
                    loginView.setBackgroundResource(R.mipmap.background);

                    // 创建按钮
                    Button button = new Button(mainContext);
                    // 设置按钮的背景图片
                    button.setBackgroundResource(R.drawable.button_selector);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    // 添加按钮
                    loginView.addView(button, params);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // 将游戏场景的常量添加到stageList集合中
                            stageList.add(Constant.STAGE_GAME);
                        }
                    });
                    // 通过Handler通知主界面加载loginView组件
                    setViewHandler.sendMessage(setViewHandler
                            .obtainMessage(0, loginView));  // ①
                }
                break;
            case Constant.LOGIC:
                break;
            case Constant.CLEAN:
                // 清除登录界面
                if (loginView != null) {
                    // 通过Handler通知主界面删除loginView组件
                    delViewHandler.sendMessage(delViewHandler
                            .obtainMessage(0, loginView));  // ②
                    loginView = null;
                }
                break;
            case Constant.PAINT:
                break;
        }
        return Constant.STAGE_NO_CHANGE;
    }

    // 定义游戏失败界面
    private RelativeLayout loseView;
    public int doLose(int step){
        switch (step) {
            case Constant.INIT:
                // 初始化失败界面
                if (loseView == null) {
                    ViewManager.soundPool.play(ViewManager.soundMap.get(Constant.GAME_OVER_SOUND), 1, 1, 0, 0, 1);

                    // 创建失败界面
                    loseView = new RelativeLayout(mainContext);
                    loseView.setBackgroundResource(R.mipmap.gameover);
                    Button button = new Button(mainContext);
                    button.setBackgroundResource(R.drawable.again);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    loseView.addView(button, params);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            // 跳转到继续游戏的界面
//                            stageList.add(Constant.STAGE_GAME);
                            stageList.add(Constant.STAGE_INIT);
                            ViewManager.soundPool.play(ViewManager.soundMap.get(Constant.BUTTON_SOUND), 1, 1, 0, 0, 1);

                        }
                    });
                    setViewHandler.sendMessage(setViewHandler
                            .obtainMessage(0, loseView));
                }
                break;
            case Constant.LOGIC:
                break;
            case Constant.CLEAN:
                // 清除界面
                if (loseView != null) {
                    delViewHandler.sendMessage(delViewHandler
                            .obtainMessage(0, loseView));
                    loseView = null;
                }
                break;
            case Constant.PAINT:
                break;
        }
        return Constant.STAGE_NO_CHANGE;
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
    public int doGame(int step) {
        switch(step) {
            case Constant.INIT:
                // 初始化游戏界面
                if (gameLayout == null) {
                    gameLayout = new RelativeLayout(mainContext);
                }
                break;
            case Constant.LOGIC:
                // 随机生成怪物
                EnemyManager.generateEnemy();
                // 检查碰撞
                EnemyManager.checkEnemy();
                // 角色碰撞
                EnemyManager.checkPlayer();
                // 角色死亡
                if (playerHero.isDie()) {

                    stageList.add(Constant.STAGE_LOSE);
                }
                break;
            case Constant.CLEAN:
                // 清除游戏界面
                if (gameLayout != null) {
                    delViewHandler.sendMessage(delViewHandler.obtainMessage(0, gameLayout));
                    gameLayout = null;
                }
                break;
            case Constant.PAINT:
                // 画游戏元素
                ViewManager.clearScreen(canvas);
                ViewManager.drawGame(canvas);
                // 添加外置字体
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/WaterwaysSeafarers.ttf"));
                paint.setTextSize(200);
                canvas.drawText(Integer.toString(playerHero.getScore()), ViewManager.SCREEN_WIDTH/2,100, paint);
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
                        allCount+=Constant.SLEEP_TIME;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            isTouchPlane = false;
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            //判断玩家飞机是否被按下
            if (x > playerHero.getPlayerX() && x < playerHero.getPlayerX() + playerHero.getHeroWidth()
                    && y > playerHero.getPlayerY() && y < playerHero.getPlayerY() + playerHero.getHeroHeight()) {
                //if(isPlay){
                isTouchPlane = true;
                //}
                return true;
            }
        }//响应手指在屏幕移动的事件
        else if(event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 1){
            //判断触摸点是否为玩家的飞机
            if(isTouchPlane){
                float x = event.getX();
                float y = event.getY();
                playerHero.setPlayerX((int)(x-playerHero.getHeroWidth()/2));
                playerHero.setPlayerY((int)(y-playerHero.getHeroHeight()/2));
                return true;
            }
        }
        return false;
    }
}
