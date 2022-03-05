package Game_Tank_multiplayer;

import javafx.scene.image.ImageView;

public class Steel {
    private int x;
    private int y;
    ImageView steel;
    public Steel(int x, int y){
        this.x = x;
        this.y = y;
        steel = new ImageView("Game_Tank_multiplayer/images/steel.jpg");
        steel.setFitHeight(40);
        steel.setFitWidth(40);
        steel.setX(x * 40);
        steel.setY(y * 40);
    }

    public ImageView getSteel() {
        return steel;
    }
}
