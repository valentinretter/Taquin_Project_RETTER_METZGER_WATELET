package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Jeux implements Serializable {
    String pathImg;
    ArrayList<Integer> grille;
    int NbCase;
    int posVide;
    private static final long serialVersionUID = 342836670337066099L;

    public Jeux(String pathSave) {
        load(pathSave);
    }

    public Jeux(String pathImg, int nbCase) throws IOException {
        this.pathImg = pathImg;

        double sqrt= Math.sqrt(nbCase);
        if ( (sqrt - Math.floor(sqrt)) == 0) {
            int sq= (int)sqrt;
            this.grille = new ArrayList<>();
            NbCase = nbCase;
        }
        else {
            this.grille = new ArrayList<>();
            this.NbCase= 9;
        }
        try {
            TraitementImg.decoupe(this.pathImg, this.NbCase);
        }
        catch (IOException e){
            this.pathImg= "./src/sample/img.jpg";
            TraitementImg.decoupe(this.pathImg, this.NbCase);
        }
        int nb= 1;
        int sq = (int) Math.sqrt(this.NbCase);
        for (int i= 1 ; i<nbCase; i++){
            this.grille.add(nb);
            nb++;
        }

    }
    private void determinerCaseVide(){
        this.posVide=(int) ((Math.random()*this.NbCase)+1);
    }

    private void melanger(){
        Collections.shuffle(this.grille);
    }

    private void init(){
            determinerCaseVide();
            melanger();
    }

    public void save(String path){

        ObjectOutputStream obj = null;

        try {
            final FileOutputStream fichier = new FileOutputStream(path+".taq");
            obj = new ObjectOutputStream(fichier);
            obj.writeObject(this);
            obj.flush();
        } catch (final java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (obj != null) {
                    obj.flush();
                    obj.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void load( String path){
        ObjectInputStream obj = null;

        try {
            final FileInputStream fichier = new FileInputStream(path);
            obj = new ObjectInputStream(fichier);
            final Jeux jeux = (Jeux) obj.readObject();
            setPathImg(jeux.getPathImg());
            setGrille(jeux.getGrille());
            setNbCase(jeux.getNbCase());
            setPosVide(jeux.getPosVide());
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (obj != null) {
                    obj.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPathImg() {
        return pathImg;
    }

    public ArrayList<Integer> getGrille() {
        return grille;
    }

    public int getNbCase() {
        return NbCase;
    }

    public int getPosVide() {
        return posVide;
    }

    private void setPathImg(String pathImg) {
        this.pathImg = pathImg;
    }

    private void setGrille(ArrayList<Integer> grille) {
        this.grille = grille;
    }

    private void setNbCase(int nbCase) {
        NbCase = nbCase;
    }

    private void setPosVide(int posVide) {
        this.posVide = posVide;
    }

    @Override
    public String toString() {
        return "Jeux{" +
                "pathImg='" + pathImg + '\'' +
                ", grille=" + grille +
                ", NbCase=" + NbCase +
                ", posVide=" + posVide +
                '}';
    }

}