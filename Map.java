package Game_Tank_multiplayer;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.*;
import java.io.*;
public class Map extends Pane implements Serializable{
    private int size;
    private char[][] map;
    private Position start;
    private ArrayList<ImageView> list;
    private ArrayList<Brick> bricks;
    public Map(Scanner input, int size){
        this.size = size;
        map = new char[size][size];
        list = new ArrayList<>();
        bricks = new ArrayList<>();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                map[i][j] = input.next().charAt(0);
                Rectangle layout = new Rectangle(j * 40, i * 40, 40,40);
                getChildren().add(layout);
                if(map[i][j] == '#'){
                    Rectangle rec = new Rectangle(j * 40, i * 40, 40, 40);
                    rec.setFill(Color.GREY);
                    getChildren().add(rec);
                }
                else if(map[i][j] == 'B'){
                    Brick brick = new Brick(j,i);
                    getChildren().add(brick.getBrick());
                    bricks.add(brick);
                }
                else if(map[i][j] == 'T'){
                    list.add(new Tree(j, i).getTree());
                }
                else if(map[i][j] =='S'){
                    getChildren().add(new Steel(j,i).getSteel());
                }
                else if(map[i][j] == 'W'){
                    getChildren().add(new Water(j, i).getWater());
                }
            }
        }
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public void modifyMap(int i, int j, char ch){
        synchronized (this) {
            map[i][j] = ch;
        }
    }

    public ArrayList<Brick> getBricks(){
        return bricks;
    }
    public void modifyMap(int i, int j){
        map[i][j] = 0;
    }
    public int getSize(){
        return size;
    }
    public Position getStartPosition(){
        return start;
    }
    public char getValueAt(int i, int j){
        return map[i][j];
    }
    public ArrayList<ImageView> getTrees(){
        return list;
    }
    public char[][] getMap(){
        return map;
    }
    public void print(){
        for(int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }
}
