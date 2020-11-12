package sample;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Taquin");
        Text n = new Text("0");
        Text p = new Text("ValouDu57");
        Text pseudo = new Text("User name : ");
        Scene scene = new Scene(root, 600, 400);
        TextFlow txt3 = (TextFlow) scene.lookup("#pseudo");

        txt3.getChildren().addAll(pseudo,p);

        Label timeLabel = (Label)scene.lookup("#label");
        timeLabel.setText("0");

        Jeux j = new Jeux("src/sample/img.jpg", 16);
        GridPane grid= new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        int taille = (int) Math.sqrt(j.getNbCase());
        int nombre = 0;
        String s = null;
        ImageView iv = null;
        for(int i=0; i<taille;i++) {
            for(int k=0; k<taille;k++) {
                s = "File:src/images/img_"+j.getGrille().get(nombre)+".jpg";
                iv = new ImageView(new Image(s));
                GridPane.setConstraints(iv, i, k);
                grid.getChildren().add(iv);
                nombre++;
            }
        }
        Pane centre = (Pane)scene.lookup("#centre");
        centre.getChildren().add(grid);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
