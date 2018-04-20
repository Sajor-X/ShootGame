package sajor.com.sounds;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

import sajor.com.shootgame.MainActivity;
import sajor.com.shootgame.R;
import sajor.com.util.Constant;

public class GameSoundPool {
    private MainActivity mainActivity;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> map;
    @SuppressLint("UseSparseArrays")
    public  GameSoundPool(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        map = new HashMap<Integer, Integer>();
        soundPool = new SoundPool(13, AudioManager.STREAM_MUSIC, 0);
    }

    public void initGameSound(){
        map.put(1, soundPool.load(mainActivity, R.raw.bullet, 1));
        map.put(2, soundPool.load(mainActivity, R.raw.enemy1_down, 1));
        map.put(3, soundPool.load(mainActivity, R.raw.enemy2_down, 1));
        map.put(4, soundPool.load(mainActivity, R.raw.enemy3_down, 1));
        map.put(5, soundPool.load(mainActivity, R.raw.game_music, 1));
        map.put(6, soundPool.load(mainActivity, R.raw.game_over, 1));
        map.put(7, soundPool.load(mainActivity, R.raw.button, 1));
    }
    public void playSound(int sound, int loop){
//        AudioManager am = (AudioManager)mainActivity.getSystemService();
//        float stramVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//        float stramMaxVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//        float volume = stramVolumeCurrent/stramMaxVolumeCurrent;
        soundPool.play(map.get(sound), 1, 1, 1, loop, 1.0f);
    }

}
