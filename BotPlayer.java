package Game_Tank_multiplayer;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.*;

public class BotPlayer implements Runnable{
    private int x;
    private int y;
    private ImageView bot;
    private Map map;
    private int lives = 1;
    private LinkedList<Character> path;
    private Lock lock;
    private Condition cross;
    private boolean isAlive = true;
    private ArrayList<Tank> players;
    BotPlayer(Map map, Lock lock, Condition cross){
        this.map = map;
        this.cross = cross;
        this.lock = lock;
        initBot();
    }
    public ImageView getBot(){
        return bot;
    }

    public void setAlive(boolean alive) {
        synchronized (this) {
            isAlive = alive;
        }
    }

    public boolean isAlive() {
        synchronized (this) {
            return isAlive;
        }
    }

    private void initBot(){
        //Set initial positions for enemy tanks
        Position[] enemyPositions = new Position[]{new Position(1, 1),
                new Position(6, 1), new Position(11, 1)};
        bot = new ImageView("Game_Tank_multiplayer/images/tank2.png");
        Position initEnemy = enemyPositions[(int)(Math.random() * 3)];
        map.modifyMap(initEnemy.getY(), initEnemy.getX(), 'b');
        x = initEnemy.getX();
        y = initEnemy.getY();
        bot.setX(x * 40);
        bot.setY(y * 40);
        bot.setFitWidth(40);
        bot.setFitHeight(40);
        map.getTrees().forEach(e -> {
            map.getChildren().remove(e);
        });
        map.getTrees().forEach(e -> map.getChildren().add(e));
        bot.setVisible(false);
    }
    public void run(){
        bot.setVisible(true);
        while(isAlive) {
            getPath();
            for (Character e : path) {
                if(!isAlive)
                    break;
                if (e == 'U') {
                    bot.setRotate(180);
                    lock.lock();
                    try {
                        while (map.getValueAt(y - 1,x) == 'B' && isAlive)
                            cross.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                    Platform.runLater(() -> {
                        bot.setY(bot.getY() - 40);
                        map.modifyMap(y,x,'0');
                        --y;
                        map.modifyMap(y,x, 'b');
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException exc) {
                        exc.getCause();
                    }
                }
                if (e == 'R') {
                    bot.setRotate(-90);
                    lock.lock();
                    try {
                        while (map.getValueAt(y,x + 1) == 'B' && isAlive)
                            cross.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                    Platform.runLater(() -> {
                        bot.setX(bot.getX() + 40);
                        map.modifyMap(y,x,'0');
                        ++x;
                        map.modifyMap(y,x, 'b');
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException exc) {
                        exc.getCause();
                    }
                }
                if (e == 'D') {
                    bot.setRotate(0);
                    lock.lock();
                    try {
                        while (map.getValueAt(y + 1,x) == 'B' && isAlive)
                            cross.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                    Platform.runLater(() -> {
                        bot.setY(bot.getY() + 40);
                        map.modifyMap(y,x,'0');
                        ++y;
                        map.modifyMap(y,x, 'b');
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException exc) {
                        exc.getCause();
                    }
                }
                if (e == 'L') {
                    bot.setRotate(90);
                    lock.lock();
                    try {
                        while (map.getValueAt(y,x - 1) == 'B' && isAlive)
                            cross.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                    Platform.runLater(() -> {
                        bot.setX(bot.getX() - 40);
                        map.modifyMap(y,x,'0');
                        --x;
                        map.modifyMap(y,x, 'b');
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException exc) {
                        exc.getCause();
                    }
                }
            }
        }
        bot.setVisible(false);
    }
    private void getPath(){
        int destX = 0;
        int destY = 0;
        double min = 1000000000000000000L;
        players = Server.players;
        for(Tank e: players) {
            int tankX = e.getX();
            int tankY = e.getY();
            double dis = Math.sqrt((tankX - x) * (tankX - x) + (tankY - y) * (tankY - y));
            if(min > dis){
                destX = tankX;
                destY = tankY;
            }
        }
        boolean[][] visited = new boolean[map.getSize()][map.getSize()];
        path = new LinkedList<>();
        Position[][] predecessor = new Position[map.getSize()][map.getSize()];
        bfs(map.getMap(), predecessor, visited, x, y, destX, destY);
        int stX = destX, stY = destY;
        while(predecessor[destY][destX] != null){
            Position position = predecessor[destY][destX];
            destX = position.getX();
            destY = position.getY();
            if(stX > destX && stY == destY){
                path.add('R');
                stX = destX;
            }
            else if(stX == destX && stY < destY){
                path.add('U');
                stY = destY;
            }
            else if(stX < destX && stY == destY){
                path.add('L');
                stX = destX;
            }
            else if(stX == destX && stY > destY){
                path.add('D');
                stY = destY;
            }
        }
        Collections.reverse(path);
        //System.out.println(path);
    }
    /*########################---BFS---#######################*/
    private void bfs(char[][] map, Position[][] predecessor, boolean[][] visited,
                     int x, int y, int destX, int destY){
        Queue<Position> q = new LinkedList<>();
        q.add(new Position(x, y));
        int dx , dy;
        while(!q.isEmpty()){
            Position pos = q.remove();
            x = pos.getX();
            y = pos.getY();
            visited[y][x] = true;
            if(y == destY && x == destX) {
                return;
            }
            //search Up
            dx = 0; dy = -1;
            if(canCross(visited,y + dy, x + dx, map)) {
                q.add(new Position(x + dx, y + dy));
                predecessor[y + dy][x + dx] = pos;
            }
            //search Right
            dx = 1; dy = 0;
            if(canCross(visited,y + dy, x + dx, map)) {
                q.add(new Position(x + dx, y + dy));
                predecessor[y + dy][x + dx] = pos;
            }
            //search Down
            dx = 0; dy = 1;
            if(canCross(visited,y + dy, x + dx, map)) {
                q.add(new Position(x + dx, y + dy));
                predecessor[y + dy][x + dx] = pos;
            }
            //search Left
            dx = -1; dy = 0;
            if(canCross(visited,y + dy, x + dx, map)) {
                q.add(new Position(x + dx, y + dy));
                predecessor[y + dy][x + dx] = pos;
            }
        }
    }
    private boolean isValid(int i, int j, char[][] map){
        return map[i][j] != 'S' && map[i][j] != 'W' && map[i][j] != '#';
    }
    private boolean canCross(boolean[][] visited, int y, int x,char[][] map){
        return !visited[y][x] && isValid(y, x, map);
    }

    //    private void drawPath(ArrayList<Position> list, int[][] path, int count){
//        for(Position e: list){
//            path[e.getY()][e.getX()] = count;
//            count++;
//        }
//    }
    public int getLives() {
        synchronized (this) {
            return this.lives;
        }
    }
    public void decreaseLives(){
        lock.lock();
        --lives;
        lock.unlock();
    }
}
