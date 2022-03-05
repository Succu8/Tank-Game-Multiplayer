package Game_Tank_multiplayer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Application {
    private Map map;
    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;
    private Scene scene;
    private Tank tank;

    public void start(Stage stage) throws FileNotFoundException {
        map = new Map(new Scanner(new File("src/game_tank_multiplayer/images/map.txt")),14);

        Position position = getValidPosition();
        map.setStart(position);
        tank = new Tank(position.getX(), position.getY(), map);
        map.getChildren().add(tank.getTank());
        map.getChildren().addAll(map.getTrees());

        scene = new Scene(map, 560,560);

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.RIGHT) {
                tank.moveRight();
                try {
                    moveClient('R');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(e.getCode() == KeyCode.UP) {
                tank.moveUp();
                try {
                    moveClient('U');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(e.getCode() == KeyCode.LEFT) {
                tank.moveLeft();
                try {
                    moveClient('L');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(e.getCode() == KeyCode.DOWN) {
                tank.moveDown();
                try {
                    moveClient('D');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(e.getCode() == KeyCode.SPACE){
                Bullet bullet = new Bullet(map, tank);
                map.getChildren().remove(tank.getTank());
                map.getChildren().removeAll(map.getTrees());
                map.getChildren().addAll(bullet.getBullet(), tank.getTank());
                map.getChildren().addAll(map.getTrees());
                try {
                    moveClient('S');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        stage.setScene(scene);
        stage.setTitle("Client");
        stage.show();
        connect();
    }
    private void connect(){
        try{
            socket = new Socket("localhost",8000);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }catch (IOException exc){
            exc.printStackTrace();
        }
        new Thread(() -> {
            try {
                output.writeInt(tank.getX());
                output.writeInt(tank.getY());
                while (input.readBoolean()){
                    int x = input.readInt();
                    int y = input.readInt();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void moveClient(char ch) throws IOException {
        output.writeChar(ch);
    }
    private Position getValidPosition(){
        Position[] positions = new Position[]{new Position(1, 9), new Position(11, 9),
                new Position(1, 11)};
        return positions[0];
    }
}
