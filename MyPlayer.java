package Game_Tank_multiplayer;

public class MyPlayer implements Player {
    private Game_Tank_multiplayer.Map map;
    private Game_Tank_multiplayer.Position playerPosition;
    private int x;
    private int y;
    public MyPlayer(Game_Tank_multiplayer.Map map){
        this.map = map;
        playerPosition = map.getStartPosition();
    }
    public MyPlayer(){
    }
    public void setMap(Map map){
        this.map = map;
    }
    public void moveRight(){
        this.x = playerPosition.getX();
        this.y = playerPosition.getY();
        if(x + 1 < map.getSize() && map.getValueAt(x + 1, y) != 1){
            playerPosition.setX(++x);
        }
    }
    public void moveLeft(){
        this.x = playerPosition.getX();
        this.y = playerPosition.getY();
        if(x > 0 && map.getValueAt(x - 1, y) != 1){
            playerPosition.setX(--x);
        }
    }
    public void moveUp(){
        this.x = playerPosition.getX();
        this.y = playerPosition.getY();
        if(y > 0 && map.getValueAt(x, y - 1) != 1){
            playerPosition.setY(--y);
        }
    }
    public void moveDown(){
        this.x = playerPosition.getX();
        this.y = playerPosition.getY();
        if(y + 1 < map.getSize() && map.getValueAt(x, y + 1) != 1){
            playerPosition.setY(++y);
        }
    }
    public Position getPosition(){
        return playerPosition;
    }
}
