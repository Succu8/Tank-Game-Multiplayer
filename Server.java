package Game_Tank_multiplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.locks.*;

public class Server extends Application {
    private Map map;
    static ArrayList<Tank> players;
    private Lock lock = new ReentrantLock();
    private BotPlayer botPlayer;
    private Condition condition = lock.newCondition();
    public void start(Stage stage) throws IOException {
        map = new Map(new Scanner(new File("src/game_tank_multiplayer/images/map.txt")),14);
        map.getChildren().addAll(map.getTrees());
        Scene scene = new Scene(map, 560, 560);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Server");
        readData();
    }
    private void readData(){
        new Thread(() -> {
           try{
               ServerSocket server = new ServerSocket(8000);
               players = new ArrayList<>();
               while(true){
                   Socket socket = server.accept();
                   new Thread(new OnlinePlayers(socket, map, players)).start();
                   if(players.size() >= 1) {
                       Platform.runLater(this::addBots);
                   }
               }
           } catch (IOException exc){
               exc.printStackTrace();
           }
        }).start();
    }
    private void addBots(){
        botPlayer = new BotPlayer(map, lock, condition);
        map.getChildren().add(botPlayer.getBot());
        new Thread(botPlayer).start();
        new Thread(() -> {
            while (botPlayer.isAlive()) {
                Platform.runLater(() -> {
                    BotBullet bullet = new BotBullet(map, botPlayer, lock, condition, players);
                    map.getChildren().addAll(bullet.getBullet());
                });
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException exc) {
                    exc.getCause();
                }
            }
        }).start();
    }
}
class OnlinePlayers implements Runnable{
    private Socket socket;
    private Map map;
    private Tank tank;
    private ArrayList<Tank> players;
    OnlinePlayers(Socket socket, Map map, ArrayList<Tank> players){
        this.socket = socket;
        this.map = map;
        this.players = players;
    }
    public void run(){
        try{
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            while(true){
                int startX = input.readInt();
                int startY = input.readInt();
                Platform.runLater(() -> {
                    tank = new Tank(startX, startY, map);
                    map.getChildren().add(tank.getTank());
                });
                synchronized (this) {
                    players.add(new Tank(startX,startY, map));
                }
                while(true){
                    char movement = input.readChar();
                    Platform.runLater(() -> {
                        moveTank(movement);
                    });
                }
            }
        }catch(IOException exc){
            exc.printStackTrace();
        }
    }
    private void moveTank(char movement){
        switch (movement){
            case 'U': tank.moveUp();break;
            case 'R': tank.moveRight();break;
            case 'D': tank.moveDown();break;
            case 'L': tank.moveLeft();break;
            case 'S': {
                Platform.runLater(() -> {
                    Bullet bullet = new Bullet(map, tank);
                    map.getChildren().remove(tank.getTank());
                    map.getChildren().removeAll(map.getTrees());
                    map.getChildren().addAll(bullet.getBullet(), tank.getTank());
                    map.getChildren().addAll(map.getTrees());
                });break;
            }
        }
    }
}
