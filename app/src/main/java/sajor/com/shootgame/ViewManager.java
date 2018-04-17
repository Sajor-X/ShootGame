package sajor.com.shootgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

import java.io.InputStream;

import sajor.com.game.Graphics;

public class ViewManager {
    // 所有图片资源
    public static Bitmap imageRes = null;
    // 地图图片
    public static Bitmap map = null;
    // 保存玩家飞机的图片组
    public static Bitmap[] playerImage = null;
    // 保存一级敌人的图片组
    public static Bitmap[] enemy1_Image = null;
    // 保存子弹的图片组
    public static Bitmap[] bulletImage = null;
    // 定义游戏对图片的缩放比例
    public static float scale = 1f;
    public static Matrix matrix = new Matrix();

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    // 地图位置
    public static int map_shift = 0;

    // 获取屏幕初始宽度、高度
    public static void initScreen(int width, int height){
        SCREEN_HEIGHT = (short) height;
        SCREEN_WIDTH  = (short) width;
    }

    // 清除屏幕的方法
    public static void clearScreen(Canvas c)
    {
        c.drawColor(Color.BLACK);
    }

    // 加载游戏图片的方法
    public static void loadResource() {
        imageRes = createBitmapByID(MainActivity.res, R.mipmap.shoot);
        // 加载地图
        Bitmap temp = createBitmapByID(MainActivity.res, R.mipmap.shoot_background);
        temp = Bitmap.createBitmap(temp, 0, 75, 480, 852, null, false);
        if (temp != null && !temp.isRecycled()) {
            int width = temp.getWidth();
            if (width != SCREEN_WIDTH && SCREEN_WIDTH != 0) {
                scale = (float) SCREEN_WIDTH / (float) width;
                map = Graphics.scale(temp, width * scale, temp.getHeight() * scale);
                temp.recycle();
            } else {
                map = temp;
            }
        }
        // 小飞机缩放
        matrix.setScale(scale, scale);

        // 加载玩家飞机, 并初始化位置
        playerImage = new Bitmap[6];
        playerImage[0] = Bitmap.createBitmap(imageRes,   0,  99, 102, 126, matrix, false);
        playerImage[1] = Bitmap.createBitmap(imageRes, 165, 360, 102, 126, matrix, false);
        playerImage[2] = Bitmap.createBitmap(imageRes, 165, 234, 102, 126, matrix, false);
        playerImage[3] = Bitmap.createBitmap(imageRes, 330, 624, 102, 126, matrix, false);
        playerImage[4] = Bitmap.createBitmap(imageRes, 330, 498, 102, 126, matrix, false);
        playerImage[5] = Bitmap.createBitmap(imageRes, 432, 624, 102, 126, matrix, false);
        GameView.playerHero.init(playerImage[0]);

        // 一级敌人
        enemy1_Image = new Bitmap[5];
        enemy1_Image[0] = Bitmap.createBitmap(imageRes, 534, 612, 57, 43, matrix, false);
        enemy1_Image[1] = Bitmap.createBitmap(imageRes, 267, 347, 57, 43, matrix, false);
        enemy1_Image[2] = Bitmap.createBitmap(imageRes, 873, 697, 57, 43, matrix, false);
        enemy1_Image[3] = Bitmap.createBitmap(imageRes, 267, 296, 57, 43, matrix, false);
        enemy1_Image[4] = Bitmap.createBitmap(imageRes, 930, 697, 57, 43, matrix, false);

        // 子弹
        bulletImage = new Bitmap[2];
        bulletImage[0] = Bitmap.createBitmap(imageRes, 1004, 987, 9, 21, matrix, false);
        bulletImage[1] = Bitmap.createBitmap(imageRes, 69, 78, 9, 21, matrix, false);
    }

    // 绘制游戏界面的方法，该方法先绘制游戏背景地图，再绘制游戏角色，最后绘制所有怪物
    public static void drawGame(Canvas canvas) {
        if (canvas == null){
            return ;
        }
        // 画地图
        if (map != null && !map.isRecycled()) {
            // 地图向后移动，飞机向前移动的幻觉
            map_shift += Constant.MAP_SHIFT;
            // 绘制map图片，也就是绘制地图
            Graphics.drawImage(canvas, map, 0, 0, 0, -map_shift , map.getWidth(), ViewManager.SCREEN_HEIGHT);

            int totalHeight = ViewManager.SCREEN_HEIGHT - map_shift;
            // 采用循环，保证地图前后可以拼接起来
            while(totalHeight < ViewManager.SCREEN_HEIGHT) {
                // 地图图片总长度
                int mapHeight = map.getHeight();
                // 要画的区域的高度（会慢慢变大）
                int drawHeight = ViewManager.SCREEN_HEIGHT - totalHeight;
                if(mapHeight < drawHeight) {
                    drawHeight = mapHeight;
                    map_shift = 0;
                }
                Graphics.drawImage(canvas, map, 0, 0, 0, mapHeight-drawHeight, map.getWidth(), drawHeight);

                totalHeight += drawHeight;
            }
        }
        // 画玩家飞机
        GameView.playerHero.drawMe(canvas);
        // 画敌人飞机
    }

    // 工具方法： 根据图片id 获取实际位图
    private static Bitmap createBitmapByID(Resources res, int resID) {
        try {
            InputStream is = res.openRawResource(resID);
            return BitmapFactory.decodeStream(is, null, null);
        } catch (Exception e) {
            return null;
        }
    }

}
