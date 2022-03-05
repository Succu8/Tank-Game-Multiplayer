package Game_Tank_multiplayer;

public interface Player {
    void moveUp();
    void moveDown();
    void moveRight();
    void moveLeft();
    void setMap(Map map);
    Position getPosition();
}
