package Game_Tank_multiplayer;

import javafx.scene.image.ImageView;

public class Water {
    private int x;
    private int y;
    private ImageView water;
    public Water(int x, int y){
        this.x = x;
        this.y = y;
        water = new ImageView("Game_Tank_multiplayer/images/water.jpg");
        water.setX(x * 40);
        water.setY(y * 40);
        water.setFitHeight(40);
        water.setFitWidth(40);
    }

    public ImageView getWater() {
        return water;
    }
}
