package Game_Tank_multiplayer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.concurrent.locks.*;


public class BotBullet {
    private Lock lock;
    private Condition cross;
    private int x;
    private int y;
    private Timeline timeline;
    private ImageView bullet;
    private ArrayList<Tank> players;
    private BotPlayer bot;
    private Map map;
    public BotBullet(Map map, BotPlayer bot, Lock lock,
                     Condition cross, ArrayList<Tank> players){
        this.map = map;
        this.bot = bot;
        x = (int)bot.getBot().getX() / 40;
        y = (int)bot.getBot().getY() / 40;
        setBullet(bot.getBot().getRotate());
        this.lock = lock;
        this.cross = cross;
        this.players = players;
        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> pew()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        //setBullet();
        timeline.play();
    }
    public void setBullet(double r){
        bullet = new ImageView("Game_Tank_multiplayer/images/bullet.png");
        bullet.setFitWidth(3);
        bullet.setFitHeight(3);
        bullet.setX(x * 40 + 20);
        bullet.setY(y * 40 + 20);
        bullet.setRotate(r);
    }
    public ImageView getBullet(){
        return bullet;
    }
    public void pew(){
        if(bullet.getRotate() == -90){
            if(x + 1 < map.getSize() && canCross(y, x + 1)){
                bullet.setX(bullet.getX() + 40);
                ++x;
            }
            else
                stop();
        }
        if(bullet.getRotate() == 90){
            if(x - 1 > 0 && canCross(y, x - 1)){
                bullet.setX(bullet.getX() - 40);
                --x;
            }
            else
                stop();
        }
        if(bullet.getRotate() == 180){
            if(y - 1 > 0 && canCross(y - 1, x)){
                bullet.setY(bullet.getY() - 40);
                --y;
            }
            else
                stop();
        }
        if(bullet.getRotate() == 0){
            if( y + 1 < map.getSize() && canCross(y + 1, x)){
                bullet.setY(bullet.getY() + 40);
                ++y;
            }
            else
                stop();
        }
    }
    private boolean canCross(int i, int j){
        if(map.getValueAt(i,j) == 'B')
            distractBrick(i,j);
        return map.getValueAt(i,j) != 'S' && map.getValueAt(i,j) != 'B'
                && map.getValueAt(i,j) != '#';
    }
    private void stop(){
        timeline.jumpTo(Duration.ZERO);
        timeline.stop();
        bullet.setVisible(false);
    }
    public void setPlayers(ArrayList<Tank> players){
        this.players = players;
    }
    private void distractBrick(int i, int j){
        ArrayList<Brick> bricks = map.getBricks();
        bricks.forEach(e -> {
            if (e.getX() == j && e.getY() == i) {
                e.decreaseLives();
                if (e.getLives() == 0) {
                    lock.lock();
                    e.getBrick().setVisible(false);
                    map.modifyMap(i, j, '0');
                    cross.signalAll();
                    lock.unlock();
                }
            }
        });
    }
    private void killTank(Tank tank){
        synchronized (this){
            tank.killTank();
            if(tank.getHP() == 0) {
                map.getChildren().remove(tank.getTank());
                players.remove(tank);
            }
        }
    }
}


