package sajor.com.util;

public class Constant {
    // 代表场景不改变的常量
    public static final int STAGE_NO_CHANGE = 0;
    // 代表初始化场景的常量
    public static final int STAGE_INIT = 1;
    // 代表登录场景的常量
    public static final int STAGE_LOGIN = 2;
    // 代表游戏场景的常量
    public static final int STAGE_GAME = 3;
    // 代表失败场景的常量
    public static final int STAGE_LOSE = 4;
    // 代表退出场景的常量
    public static final int STAGE_QUIT = 5;
    // 代表错误场景的常量
    public static final int STAGE_ERROR = 6;

    // 步骤：初始化
    public static final int INIT = 1;
    // 步骤：逻辑
    public static final int LOGIC = 2;
    // 步骤：清除
    public static final int CLEAN = 3;
    // 步骤：画
    public static final int PAINT = 4;

    // 两次调度之间默认的暂停时间
    public static final int SLEEP_TIME = 50;
    // 最小的暂停时间
    public static final int MIN_SLEEP = 5;

    // 地图偏移速度
    public static final int MAP_SHIFT = 6;

    // 子弹类型
    public static final int BULLET_TYPE_1 = 1;
    public static final int BULLET_TYPE_2 = 2;
    // 子弹速度
    public static final int BULLET_SPEED = 50;

    // 定义代表敌人类型的常量（如果程序还需要增加更多怪物，只需在此处添加常量即可）
    public static final int ENEMY_TYPE_1 = 100;
    public static final int ENEMY_TYPE_2 = 500;
    public static final int ENEMY_TYPE_3 = 1000;
    // 定义代表敌人生命值的常量
    public static final int ENEMY_HP_1 = 1;
    public static final int ENEMY_HP_2 = 8;
    public static final int ENEMY_HP_3 = 15;
    // 敌人速度
    public static final int ENEMY1_SPEED = 15;
    public static final int ENEMY2_SPEED = 5;
    public static final int ENEMY3_SPEED = 2;

    // 音乐
    public static final int GAME_MUSIC_SOUND = 11;
    public static final int GAME_OVER_SOUND = 21;
    public static final int BULLET_SOUND = 31;
    public static final int FLYING_SOUND = 41;
    public static final int BUTTON_SOUND = 51;

}
