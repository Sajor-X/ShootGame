package sajor.com.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import sajor.com.util.Constant;
import sajor.com.util.Graphics;
import sajor.com.view.ViewManager;

public class Enemy {
    // 定义敌人类型的成员变量
    private int enemyType;

    // 定义敌人X、Y坐标的成员变量
    private int enemyX;
    private int enemyY;

    // 定义玩家图片大小
    private int enemyHeight = 0;
    private int enemyWidth = 0;

    // 定义敌人是否已经死亡的旗标
    public boolean enemyIsDie = false;

    // 定义当前正在绘制敌人动画的第几帧的变量
    private int drawIndex = 0;

    // 当怪物的死亡动画帧播放完成时，该变量的值变为0。
    private int dieMaxDrawCount = Integer.MAX_VALUE;

    public Enemy(int type, int x){
        this.enemyX = x;
        this.enemyY = 0;  // 默认屏幕顶端
        this.enemyType = type;
        init();
    }

    public void init(){
        Bitmap bitmap = null;
        switch (enemyType)
        {
            case Constant.ENEMY_TYPE_1:
                bitmap = ViewManager.enemy1_Image[0];
                break;
            case Constant.ENEMY_TYPE_2:
                bitmap = ViewManager.enemy1_Image[1];
                break;
            case Constant.ENEMY_TYPE_3:
                bitmap = ViewManager.enemy1_Image[2];
                break;
            default:
                break;
        }
        this.enemyHeight = bitmap.getHeight();
        this.enemyWidth = bitmap.getWidth();
    }

    // 定义敌人移动的方法
    public void enemyMove(){
        switch (enemyType) {
            case Constant.ENEMY_TYPE_1:
                enemyY += Constant.ENEMY1_SPEED;break;
            case Constant.ENEMY_TYPE_2:
                enemyY += Constant.ENEMY2_SPEED;break;
            case Constant.ENEMY_TYPE_3:
                enemyY += Constant.ENEMY3_SPEED;break;
            default:
                break;
        }
    }

    // 画敌人的方法
    public void enemyDraw(Canvas canvas)
    {
        if (canvas == null) {
            return;
        }
        switch (enemyType)
        {
            case Constant.ENEMY_TYPE_1:
                drawAni(canvas, ViewManager.enemy1_Image);
                break;
            case Constant.ENEMY_TYPE_2:
                drawAni(canvas, ViewManager.enemy2_Image);
                break;
            case Constant.ENEMY_TYPE_3:
                drawAni(canvas, ViewManager.enemy3_Image);
                break;
            default:
                break;
        }
    }

    // 根据敌人的动画帧图片来绘制怪物动画
    public void drawAni(Canvas canvas, Bitmap[] bitmapArr) {
        if (canvas == null) {
            return;
        }
        if (bitmapArr == null) {
            return;
        }
        Bitmap bitmap = bitmapArr[drawIndex];
        Graphics.drawImage(canvas, bitmap, getEnemyX(), getEnemyY(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        enemyMove();

        //        // 如果敌人已经死，且没有播放过死亡动画
//        //（dieMaxDrawCount等于初始值表明未播放过死亡动画）
//        if (isDie && dieMaxDrawCount == Integer.MAX_VALUE) {
//            // 将dieMaxDrawCount设置与死亡动画的总帧数相等
//            dieMaxDrawCount = bitmapArr.length;
//        }
//        drawIndex = drawIndex % bitmapArr.length;
//        // 获取当前绘制的动画帧对应的位图
//        Bitmap bitmap = bitmapArr[drawIndex];
//        if (bitmap == null || bitmap.isRecycled()) {
//            return;
//        }
//
//        enemyMove();
//        // 画敌人动画帧的位图
//        Graphics.drawImage(canvas, bitmap, getEnemyX(), getEnemyY(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//        // 每播放死亡动画的一帧，dieMaxDrawCount减1。
//        // 当dieMaxDrawCount等于0时，表明死亡动画播放完成，MonsterManger会删除该敌人。
//        if (isDie) {
//            dieMaxDrawCount--;
//        }

    }

    public boolean isHurt(int x, int y){
        if(x >= enemyX && x <= enemyWidth + enemyX && y >= enemyY && y <= enemyHeight + enemyY){
            return true;
        }
        return false;
    }

    public int getEnemyX() {
        return enemyX;
    }

    public int getDieMaxDrawCount() {
        return dieMaxDrawCount;
    }

    public int getEnemyY() {
        return enemyY;
    }

    public void setEnemyIsDie(boolean enemyIsDie) {
        this.enemyIsDie = enemyIsDie;
    }

    public int getEnemyType() {
        return enemyType;
    }

    public boolean isEnemyIsDie() {
        return enemyIsDie;
    }
}
