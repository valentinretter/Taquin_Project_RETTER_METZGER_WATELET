package sample;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class Controller{


    private Timer timer = new Timer();
    private Task timerTask;
    private javax.swing.Timer blinkTask;
    private boolean debut=true;
    @FXML
    private Label label, nbshots;
    @FXML
    private ToggleButton play;
    @FXML
    private GridPane grille;
    @FXML
    private Pane pane;
    @FXML
    private RadioButton neuf, seize, vingtcinq, trentesix, photo1, photo2, photo3;
    @FXML
    private TextField pseudo;
    @FXML
    private ProgressBar progress;

    /**
     * methode d'affichage d'une fenêtre pop-up avec un titre et une phrase d'instruction
     * @param t
     * @param instruction
     */
    public void chargerPopUp(String t, String instruction){
        Stage popUpStage = new Stage();
        Parent root = null;
        popUpStage.setTitle(t);
        try {
            root = FXMLLoader.load(getClass().getResource("popUp.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene sc = new Scene(root);
        Label instructions = (Label) sc.lookup("#instructions");
        instructions.setText(instruction);
        popUpStage.setScene(sc);
        popUpStage.initModality(Modality.APPLICATION_MODAL);    // popup
        popUpStage.showAndWait();
    }

    /**
     * affichage de la fenêtre pop-up help
     * @param actionEvent
     */
    public void sayHelp(ActionEvent actionEvent) {
        chargerPopUp("Help", "Cliquez sur [IA] \n pour vous aider à résoudre le jeu.");
    }

    /**
     * affichage de la fenêtre Wikipedia
     * @param actionEvent
     */
    public void hyperLink(ActionEvent actionEvent) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("What's Taquin ?");
        WebView browser = new WebView();
        browser.getEngine().load("https://en.wikipedia.org/wiki/15_puzzle");
        VBox vBox = new VBox(browser);
        Scene scene = new Scene(vBox, 960, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * methode de lancement du jeu
     * vérifie si le pseudo a été rentré
     * @param actionEvent
     * @throws InterruptedException
     */
    @FXML
    public void playTaquin(ActionEvent actionEvent) throws InterruptedException {
           if(debut){
               if(pseudo.getText().equals("")){
                   pseudo.setStyle("-fx-border-color : red");
                   chargerPopUp("[!]", "Veuillez entrer votre pseudonyme \n dans la case en haut à droite.");
               }
               play.setText("Stop");
               start();
               debut = false;
           }else {
               if (play.isSelected()) {
                   play.setText("Stop");
                   resume();
               } else {
                   play.setText("Play");
                   pause();
               }
           }

    }

    /**
     * methode de déplacement des cases
     * vérifie si toutes les cases sont dans le bon ordre
     * et si oui, réinitialise le jeu
     * @param tab
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    public void bouger(int[] tab) throws IOException, SQLException {
        int[] tabPos = tab;
        if(tabPos!=null) {
            String s = Main.getJ().getGrille().get(tabPos[0]).getPathImg();
            String s1 = Main.getJ().getGrille().get(tabPos[1]).getPathImg();
            ImageView iv = new ImageView(new Image(s));
            ImageView iv1 = new ImageView(new Image(s1));
            int length = grille.getColumnCount();
            int x =0, y =0, x1=0, y1=0;
            for(int i=2; i<=length;i++){
                if((tabPos[0])/length <1){
                    x=tabPos[0];
                    break;
                }else if((tabPos[0])/length <i){
                    x=tabPos[0]-length*(i-1);
                    y=i-1;
                    break;
                }
            }
            for(int i=2; i<=length;i++){
                if((tabPos[1])/length <1){
                    x1=tabPos[1];
                    break;
                }else if((tabPos[1])/length <i){
                    x1=tabPos[1]-length*(i-1);
                    y1=i-1;
                    break;
                }
            }
            grille.add(iv, x, y);
            grille.add(iv1, x1, y1);
            nbshots.setText("" + Main.getJ().getNbshots());
            if (progress.getProgress() <= 1) {
                progress.setProgress(Main.getJ().getNbshots() * 0.01);
            }
            if (progress.getProgress() > 0.3 && progress.getProgress() < 0.6) {
                progress.setStyle("-fx-accent : orange");
            }
            if (progress.getProgress() >= 0.6) {
                progress.setStyle("-fx-accent : red");
            }
            if (progress.getProgress() == 1) {
                progress.setStyle("-fx-accent : black");
            }
            if(Main.getJ().estResolue(Main.getJ().getGrille())){
                JDBC.getConnexion();
                JDBC.addPartie(pseudo.getText(), ((int) Math.pow(Main.getJ().getNbCase(), 3)/(Main.getJ().getNbshots()*timerTask.getSeconds())));
                chargerPopUp("Congratulations", "Félicitation ! Vous avez terminé le jeu !");
                chargerImage("src/sample/img.jpg");
            }
        }
    }

    /**
     * methode de réinitialisation du jeu
     */
    public void restart(){
       if(!play.getText().equals("Start")){stop();}
        play.setSelected(false);
        play.setText("Start");
        if(pseudo.getText().equals("")) {
            debut = true;
        }
    }

    /**
     * methode de déplacement vers le haut
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    public void up(ActionEvent actionEvent) throws IOException, SQLException {
        if(play.isSelected()) {
            int[] tabPos = Main.getJ().move('Z');
            bouger(tabPos);
        }
    }

    /**
     * methode de déplacement vers la gauche
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void left(ActionEvent actionEvent) throws IOException, SQLException {
        if(play.isSelected()){
            int[] tabPos = Main.getJ().move('Q');
            bouger(tabPos);
        }
    }

    /**
     * methode de déplacement vers la droite
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void right(ActionEvent actionEvent) throws IOException, SQLException {
        if(play.isSelected()) {
            int[] tabPos = Main.getJ().move('D');
            bouger(tabPos);
        }
    }

    /**
     * methode de déplacement vers le bas
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void down(ActionEvent actionEvent) throws IOException, SQLException {
       if(play.isSelected()) {
           int[] tabPos = Main.getJ().move('S');
           bouger(tabPos);
       }
    }

    /**
     * methode de sauvegarde de la partie
     * @param actionEvent
     */
    public void save(ActionEvent actionEvent) {
        stop();
        Main.getJ().save("Saved");
    }

    /**
     * methode qui génère une nouvelle grille d'une taille sélectionnée
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void generateGrid(ActionEvent actionEvent) throws IOException {
       grille.getChildren().clear();
       JeuxConsole j2 = null;
       if(neuf.isSelected()) {
           j2 = new JeuxConsole("src/sample/img.jpg", 9);
           Main.setJ(j2);
           if(grille.getColumnCount()==6){
               grille.getColumnConstraints().remove(5);
               grille.getRowConstraints().remove(5);
           }
           if(grille.getColumnCount()>=5){
               grille.getColumnConstraints().remove(4);
               grille.getRowConstraints().remove(4);
           }
           if(grille.getColumnCount()>=4){
               grille.getColumnConstraints().remove(3);
               grille.getRowConstraints().remove(3);
           }
       }
       if(seize.isSelected()){
           j2 = new JeuxConsole("src/sample/img.jpg", 16);
           Main.setJ(j2);
           if(grille.getColumnCount()==6){
               grille.getColumnConstraints().remove(5);
               grille.getRowConstraints().remove(5);
           }
           if(grille.getColumnCount()>=5){
               grille.getColumnConstraints().remove(4);
               grille.getRowConstraints().remove(4);
           }
           if(grille.getColumnCount()==3){

           }
       }
        if(vingtcinq.isSelected()){
            j2 = new JeuxConsole("src/sample/img.jpg", 25);
            Main.setJ(j2);
            if(grille.getColumnCount()==6){
                grille.getColumnConstraints().remove(5);
                grille.getRowConstraints().remove(5);
            }
            if(grille.getColumnCount()<5){
                grille.getColumnConstraints().add(new ColumnConstraints());
                grille.getRowConstraints().add(new RowConstraints());
            }
            if(grille.getColumnCount()<4){
                grille.getColumnConstraints().add(new ColumnConstraints());
                grille.getRowConstraints().add(new RowConstraints());
            }
        }
        if(trentesix.isSelected()){
            j2 = new JeuxConsole("src/sample/img.jpg", 36);
            Main.setJ(j2);
            if(grille.getColumnCount()<6){
                grille.getColumnConstraints().add(new ColumnConstraints());
                grille.getRowConstraints().add(new RowConstraints());
            }
            if(grille.getColumnCount()<5){
                grille.getColumnConstraints().add(new ColumnConstraints());
                grille.getRowConstraints().add(new RowConstraints());
            }
            if(grille.getColumnCount()<4){
                grille.getColumnConstraints().add(new ColumnConstraints());
                grille.getRowConstraints().add(new RowConstraints());
            }
        }
                int taille = (int) Math.sqrt(j2.getNbCase());
                int nombre = 0;
                String s = null;
                ImageView iv = null;
                for (int i = 0; i < taille; i++) {
                    for (int k = 0; k < taille; k++) {
                        s = j2.getGrille().get(nombre).getPathImg();
                        nombre++;
                        iv = new ImageView(new Image(s));
                        GridPane.setConstraints(iv, i, k);
                        grille.getChildren().add(iv);
                    }
                }
                restart();
    }


    /**
     * methode qui applique le theme négatif
     * @param actionEvent
     */
    @FXML
    public void negative(ActionEvent actionEvent) {
        pane.setBlendMode(BlendMode.DIFFERENCE);
    }

    /**
     * methode qui applique le theme rose
     * @param actionEvent
     */
    public void pink(ActionEvent actionEvent) {
        pane.setBlendMode(BlendMode.GREEN);
    }

    /**
     * methode qui applique le theme par default
     * @param actionEvent
     */
    public void bauhaus(ActionEvent actionEvent) {
        pane.setBlendMode(BlendMode.SRC_OVER);
    }

    /**
     * methode qui modifie l'image de la grille
     * @param actionEvent
     * @throws IOException
     */
    public void download(ActionEvent actionEvent) throws IOException {
       if(photo3.isSelected()) {
           JFileChooser fc = new JFileChooser();
           fc.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg"));
           fc.setAcceptAllFileFilterUsed(false);
           File f = null;
           if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
               f = fc.getSelectedFile();
           }
           if (f != null) {
               chargerImage(f.getPath());
           }
       }
       if(photo1.isSelected()){
           chargerImage("src/sample/img.jpg");}
       if(photo2.isSelected()){}
    }

    /**
     * methode qui charge la nouvelle image à appliquer dans la grille
     * @param path
     * @throws IOException
     */
    public void chargerImage(String path) throws IOException {
       grille.getChildren().clear();
       JeuxConsole j3 = null;
       if(neuf.isSelected()){j3 = new JeuxConsole(path, 9);}
       if(seize.isSelected()){j3 = new JeuxConsole(path, 16);}
       if(vingtcinq.isSelected()){j3 = new JeuxConsole(path, 25);}
       if(trentesix.isSelected()){j3 = new JeuxConsole(path, 36);}
       Main.setJ(j3);
        int taille = (int) Math.sqrt(j3.getNbCase());
        int nombre = 0;
        String s = null;
        ImageView iv = null;
        for(int i=0; i<taille;i++) {
            for(int k=0; k<taille;k++) {
                s = j3.getGrille().get(nombre).getPathImg();
                nombre++;
                iv = new ImageView(new Image(s));
                grille.add(iv,i,k);
            }
        }
        restart();
    }

    /**
     * methode qui aaplique un encadré rougé à la case contenant le pseudo
     * @param keyEvent
     */
    public void pseudoChanged(KeyEvent keyEvent) {
        pseudo.setStyle("-fx-border-color : green");
    }

    /**
     * methode qui affiche la fenêtre du classement
     * @param actionevent
     * @throws SQLException
     */
    public void rank(ActionEvent actionevent) throws SQLException {
        Stage popUpStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("rank.fxml"));
        Parent root = null;
        popUpStage.setTitle("Classement");
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TableView<String> tableView = new TableView();
        JDBC.getConnexion();
        TreeMap<String, Integer> tm = JDBC.classement();

        tableView.getItems().addAll(tm.keySet());

        TableColumn<String, String> keyColumn = new TableColumn<>("Pseudo");
        keyColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()));
        TableColumn<String, String> valueColumn = new TableColumn<>("Score");
        valueColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()));

        tableView.getColumns().addAll(keyColumn, valueColumn);
        Scene sc = new Scene(root);
        Pane pane2 = (Pane) sc.lookup("#pane2");
        pane2.getChildren().add(tableView);
        popUpStage.setScene(sc);
        popUpStage.initModality(Modality.APPLICATION_MODAL);    // popup
        popUpStage.showAndWait();
    }

    /**
     * methode qui appelle l'IA
     * @param actionEvent
     * @throws InterruptedException
     * @throws IOException
     * @throws SQLException
     */
    public void resolution(ActionEvent actionEvent) throws InterruptedException, IOException, SQLException {
       if(play.isSelected()) {
           int[] tabPos = Main.getJ().moveIa(Main.getJ().resolution());
           bouger(tabPos);
       }
    }


    /**
     * chronomètre
     */
    private class Task extends TimerTask {

        final long start;

        public int getSeconds() {
            return seconds;
        }

        private int seconds = 0;
        private boolean running;
        public Task() {
            this(System.currentTimeMillis());
        }
        public Task(Task task) {
            this(task.start);
        }
        public Task(long start) {
            this.start=start;
        }

        @Override
        public boolean cancel() {
            final boolean cancel = super.cancel();
            running=false;
            return cancel;
        }

        public boolean isRunning() {
            return running;
        }

        @Override
        public void run() {
            running=true;
            final long time = System.currentTimeMillis() - start;
            seconds = (int) (time / 1000);
            final String message = String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
            Platform.runLater(new Runnable() {
                public void run() {
                    label.setText(message);
                }
            });
        }

    };



    private void start() {
        blinkStop();
        timerTask = new Task();
        timer.schedule(timerTask, 1000, 1000);
    }

    private void pause() {
        timerTask.cancel();
        blinkStart();
    }

    private void resume() {
        blinkStop();
        timerTask=new Task(timerTask);
        timer.schedule(timerTask, 1000, 1000);
    }

    private void stop() {
        timerTask.cancel();
        blinkStop();
        timer.purge();
    }

    private void blinkStart() {
        if ( blinkTask!=null ) {
            blinkTask.stop();
        }
        blinkTask = new javax.swing.Timer(1000, e-> label.setVisible(!label.isVisible()));
        blinkTask.start();
    }

    private void blinkStop() {
        if ( blinkTask!=null ) {
            blinkTask.stop();
            blinkTask=null;
            label.setVisible(true);
        }
    }




}
