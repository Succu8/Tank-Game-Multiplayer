package Game_Tank_multiplayer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;


public class Bullet {
    private int x;
    private int y;
    private Timeline timeline;
    private ImageView bullet;
    private Game_Tank_multiplayer.Tank tank;
    private Game_Tank_multiplayer.Map map;
    public Bullet(Map map, Tank tank){
        this.tank = tank;
        this.map = map;
        x = (int)tank.getTank().getX() / 40;
        y = (int)tank.getTank().getY() / 40;
        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> pew()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        setBullet();
        timeline.play();
    }
    public void setBullet(){
        int coordinate = (int)tank.getTank().getRotate();
        bullet = new ImageView("Game_Tank_multiplayer/images/bullet.png");
        bullet.setFitWidth(3);
        bullet.setFitHeight(3);
        bullet.setX(tank.getTank().getX() + 20);
        bullet.setY(tank.getTank().getY() + 20);
        bullet.setRotate(coordinate);
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
    private void distractBrick(int i, int j){
        ArrayList<Brick> bricks = map.getBricks();
        bricks.forEach(e -> {
            if(e.getX() == j && e.getY() == i){
                e.decreaseLives();
                if(e.getLives() == 0) {
                    e.getBrick().setVisible(false);
                    map.modifyMap(i,j);
                }
            }
        });
    }
}
