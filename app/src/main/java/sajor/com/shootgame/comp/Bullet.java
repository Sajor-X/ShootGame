package sajor.com.shootgame.comp;

import android.graphics.Bitmap;

import sajor.com.shootgame.Constant;
import sajor.com.shootgame.ViewManager;

public class Bullet {
    // 子弹的坐标
    private int bulletX;
    private int bulletY;

    // 子弹是否有效
    private boolean isEffect = true;

    // 子弹的类型
    private int type;

    public Bullet(int x, int y, int type){
        this.bulletX = x;
        this.bulletY = y;
        this.type = type;
    }

    // 根据类型获取子弹图片
    public Bitmap getBitmap(){
        switch (type){
            case Constant.BULLET_TYPE_1:
                return ViewManager.bulletImage[0];
            case Constant.BULLET_TYPE_2:
                return ViewManager.bulletImage[1];
            default:
                return null;
        }
    }

    // 定义子弹移动的方法
    public void move(){
        bulletY -= Constant.BULLET_SPEED;
    }

    public int getBulletY() {
        return bulletY;
    }

    public int getBulletX() {
        return bulletX;
    }

    public void setBulletX(int bulletX) {
        this.bulletX = bulletX;
    }

    public boolean isEffect() {
        return isEffect;
    }

    public void setEffect(boolean effect) {
        isEffect = effect;
    }
}
