package sajor.com.shootgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import sajor.com.object.Bullet;
import sajor.com.object.Enemy;
import sajor.com.util.Constant;
import sajor.com.util.Util;
import sajor.com.view.GameView;
import sajor.com.view.ViewManager;

public class EnemyManager {
    // 保存所有死掉的敌人，保存它们是为了绘制死亡的动画，绘制完后清除这些敌人
    public static final List<Enemy> dieEnemyList = new ArrayList<>();
    // 保存所有活着的敌人
    public static final List<Enemy> enemyList = new ArrayList<>();

    // 随机生成、并添加敌人的方法
    public static void generateEnemy()
    {
        if (enemyList.size() < 3 + Util.rand(3))
        {
            // 创建新敌人
            Enemy enemy = new Enemy(Constant.ENEMY_TYPE_1, Util.rand(ViewManager.SCREEN_WIDTH-20));
            enemyList.add(enemy);
        }
    }

    // 判断敌人越界
    public static void checkEnemyY() {
        Enemy enemy = null;
        // 定义一个集合，保存所有将要被删除的敌人
        List<Enemy> delList = new ArrayList<>();
        // 遍历怪物集合
        for (int i = 0; i < enemyList.size(); i++) {
            enemy = enemyList.get(i);
            if (enemy == null) {
                continue;
            }
            // 如果敌人的Y坐标越界，将怪物添加delList集合中
            if (enemy.getEnemyY() > ViewManager.SCREEN_HEIGHT) {
                delList.add(enemy);
            }
        }
        // 删除所有delList集合中所有怪物
        enemyList.removeAll(delList);
        delList.clear();
    }

    // 绘制所有怪物的方法
    public static void drawEnemy(Canvas canvas) {
        checkEnemyY();
        Enemy enemy = null;
        // 遍历所有活着的怪物，绘制活着的怪物
        for (int i = 0; i < enemyList.size(); i++) {
            enemy = enemyList.get(i);
            if (enemy == null) {
                continue;
            }
            // 画
            if(!enemy.isEnemyIsDie()){
                enemy.enemyDraw(canvas);
            }

        }

        List<Enemy> delList = new ArrayList<>();
        // 遍历所有已死亡的怪物，绘制已死亡的怪物
        for (int i = 0; i < dieEnemyList.size(); i++) {
            enemy = dieEnemyList.get(i);
            if (enemy == null) {
                continue;
            }
            // 画
            //enemy.enemyDraw(canvas);
            // 当怪物的getDieMaxDrawCount()返回0时，表明该怪物已经死亡，
            // 且该怪物的死亡动画所有帧都播放完成，将它们彻底删除。
            if (enemy.getDieMaxDrawCount() <= 0) {
                delList.add(enemy);
            }
        }
        dieEnemyList.removeAll(delList);
    }

    // 检查敌人是否将要死亡的方法
    public static void checkEnemy(){
        // 获取玩家发射的所有子弹
        List<Bullet> bulletList = GameView.playerHero.getBulletList();
        if (bulletList == null) {
            bulletList = new ArrayList<>();
        }
        Enemy enemy = null;
        // 定义一个delList集合，用于保存将要死亡的敌人
        List<Enemy> delEnemyList = new ArrayList<>();
        // 定义一个delBulletList集合，用于保存所有将要被删除的子弹
        List<Bullet> delBulletList = new ArrayList<>();
        // 遍历所有子弹
        for (int i = 0; i < bulletList.size(); i++)
        {
            enemy = enemyList.get(i);
            if (enemy == null) {
                continue;
            }

            // 遍历角色发射的所有子弹
            for (Bullet bullet : bulletList) {
                if (bullet == null || !bullet.isEffect()) {
                    continue;
                }
                // 如果敌人被角色的子弹打到
                if (enemy.isHurt(bullet.getBulletX(), bullet.getBulletY())) {
                    // 将子弹设为无效
                    bullet.setEffect(false);
                    // 将敌人设为死亡状态
                    enemy.setEnemyIsDie(true);

//                    // 如果敌人是
//                    if(enemy.getEnemyType() == Constant.ENEMY_TYPE_1) {
//                        // 播放惨叫音效
//                        ViewManager.soundPool.play(ViewManager.soundMap.get(3), 1, 1, 0, 0, 1);
//                    }
                    // 将敌人（被子弹打中的敌人）添加到delList集合中
                    delEnemyList.add(enemy);
                    // 将打中敌人的子弹添加到delBulletList集合中
                    delBulletList.add(bullet);
                }
            }
            // 将delBulletList包含的所有子弹从bulletList集合中删除
            bulletList.removeAll(delBulletList);
        }
        // 将已死亡的敌人（保存在delEnemyList集合中）添加到dieEnemyList集合中
        dieEnemyList.addAll(delEnemyList);
        // 将已死亡的敌人（保存在delEnemyList集合中）从enemyList集合中删除
        enemyList.removeAll(delEnemyList);
    }
}
