package Game_Tank_multiplayer;


import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Tank extends MyPlayer{
    private int x;
    private int y;
    private int lives = 3;
    private ArrayList<ImageView> list;
    private Position currentPosition;
    private Map map;
    private ImageView tank;
    public Tank(int x, int y, Map map){
        this.x = x;
        this.y = y;
        this.map = map;
        //initLives();
        currentPosition = new Position(x, y);
        tank = new ImageView("Game_Tank_multiplayer/images/tank.png");
        tank.setFitWidth(40);
        tank.setFitHeight(40);
        tank.setX(x * 40);
        tank.setY(y * 40);
    }
    public ImageView getTank() {
        return tank;
    }
    public void moveRight(){
        x = currentPosition.getX();
        y = currentPosition.getY();
        tank.setRotate(-90);
        if(x + 1 < map.getSize() && canPass(y, x + 1)){
            tank.setX(tank.getX() + 40);
            currentPosition.setX(++x);
        }
    }
    public void moveLeft(){
        x = currentPosition.getX();
        y = currentPosition.getY();
        tank.setRotate(90);
        if(x - 1 > 0 && canPass(y, x - 1)){
            tank.setX(tank.getX() - 40);
            currentPosition.setX(--x);
        }
    }
    public void moveUp(){
        x = currentPosition.getX();
        y = currentPosition.getY();
        tank.setRotate(180);
        if(y - 1 > 0 && canPass(y - 1, x)){
            tank.setY(tank.getY() - 40);
            currentPosition.setY(--y);
        }
    }
    public void moveDown(){
        x = currentPosition.getX();
        y = currentPosition.getY();
        tank.setRotate(0);
        if(y + 1 < map.getSize() && canPass(y + 1, x)){
            tank.setY(tank.getY() + 40);
            currentPosition.setY(++y);
        }
    }
    private boolean canPass(int i, int j){
        return map.getValueAt(i,j) != 'S' && map.getValueAt(i,j) != 'B'
                && map.getValueAt(i,j) != 'W' && map.getValueAt(i,j) != '#';
    }
    public ArrayList<ImageView> getLives(){
        return list;
    }
    private void initLives(){
        list = new ArrayList<>();
        for(int i = 0; i < lives; i++){
            ImageView heart = new ImageView("Game_Tank_multiplayer/images/hp.png");
            int x = map.getSize() - 2;
            int y = map.getSize() - map.getSize() / 3;
            heart.setX(x * 40 + i * 20 + 5);
            heart.setY(y * 40);
            heart.setFitHeight(20);
            heart.setFitWidth(20);
            list.add(heart);
        }
        map.getChildren().addAll(list);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public void killTank(){
        --lives;
    }
    public int getHP(){
        return lives;
    }
}
