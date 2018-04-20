package sajor.com.shootgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import sajor.com.object.Bullet;
import sajor.com.object.Enemy;
import sajor.com.object.Player;
import sajor.com.util.Constant;
import sajor.com.util.Util;
import sajor.com.view.GameView;
import sajor.com.view.ViewManager;

public class EnemyManager {
    // 保存所有死掉的敌人，保存它们是为了绘制死亡的动画，绘制完后清除这些敌人
    public static final List<Enemy> dieEnemyList = new ArrayList<>();
    // 保存所有活着的敌人
    public static final List<Enemy> enemyList = new ArrayList<>();
    // 保存等级飞机数量
    public static int count2=0, count3=0;
    // 随机生成、并添加敌人的方法
    public static void generateEnemy()
    {
        int r = Util.rand(100);
        Enemy enemy=null;
        // 分数越高 敌人出现越快
        if(GameView.allCount >= (800-GameView.playerHero.getScore()/1000)){
            // 创建新敌人  比例1/2/7
            switch (r/10) {
                case 0:
                    if(count3 < Constant.ENEMY3_SPEED){
                        count3++;
                        enemy = new Enemy(Constant.ENEMY_TYPE_3);
                    } break;
                case 1:
                case 2:
                    if(count2 < Constant.ENEMY2_SPEED){
                        count2++;
                        enemy = new Enemy(Constant.ENEMY_TYPE_2);
                    } break;
            }
            if(enemy==null){
                enemy = new Enemy(Constant.ENEMY_TYPE_1);
            }
            GameView.allCount=0;
        }
        if(enemy!=null){
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

    // 绘制所有敌人的方法
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
            enemy.enemyDraw(canvas);
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
        // 遍历所有敌人
        for (int i = 0; i < enemyList.size(); i++)
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
                if (enemy.isHurt(bullet.getBulletX()+bullet.getBitmap().getWidth()/2, bullet.getBulletY()+bullet.getBitmap().getHeight()/2)) {
                    // 将子弹设为无效
                    bullet.setEffect(false);
                    enemy.setEnemyHp(enemy.getEnemyHp()-1);
                    // 将打中敌人的子弹添加到delBulletList集合中
                    delBulletList.add(bullet);
                    if(enemy.getEnemyHp() <= 0){
                        // 将敌人设为死亡状态
                        enemy.setEnemyIsDie(true);
                        // 将敌人（被子弹打中的敌人）添加到delList集合中
                        delEnemyList.add(enemy);
                        // 加分
                        GameView.playerHero.setScore(GameView.playerHero.getScore() + enemy.getEnemyType());
                        // 如果敌人是
                        switch (enemy.getEnemyType()) {
                            case Constant.ENEMY_TYPE_1:
                                // 播放惨叫音效
                                ViewManager.soundPool.play(ViewManager.soundMap.get(Constant.ENEMY_TYPE_1), 1, 1, 0, 0, 1);
                                break;
                            case Constant.ENEMY_TYPE_2:
                                count2--;
                                ViewManager.soundPool.play(ViewManager.soundMap.get(Constant.ENEMY_TYPE_2), 1, 1, 0, 0, 1);
                                break;
                            case Constant.ENEMY_TYPE_3:
                                count3--;
                                ViewManager.soundPool.play(ViewManager.soundMap.get(Constant.ENEMY_TYPE_3), 1, 1, 0, 0, 1);
                                break;
                        }
                    }

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

    // 检查玩家是否将要死亡的方法
    public static void checkPlayer(){
        Enemy enemy = null;
        // 遍历所有敌人
        for (int i = 0; i < enemyList.size(); i++)
        {
            enemy = enemyList.get(i);
            if (enemy == null) {
                continue;
            }
            Player player = GameView.playerHero;
            if(isHit(enemy, player)){
                // 死亡
                GameView.playerHero.setDie(true);
            }
        }
    }

    // 判断飞机是否碰撞
    public static boolean isHit(Enemy enemy, Player player){
        if (enemy.getEnemyX() > player.getPlayerX()+player.getHeroWidth() || enemy.getEnemyX()+enemy.getEnemyWidth() < player.getPlayerX() || enemy.getEnemyY() > player.getPlayerY()+player.getHeroHeight() || enemy.getEnemyY()+enemy.getEnemyHeight() < player.getPlayerY()){
            return false;
        }
        return true;
    }
}
