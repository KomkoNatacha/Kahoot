package TPKahoot;

/**
 * Classe représentant un joueur dans le jeu Kahoot.
 * Stocke le nom du joueur et ses statistiques de jeu.
 */
public class Player {
    private String name;    // Le nom du joueur
    private int BRep = 0;   // Nombre de bonnes réponses
    private int MRep = 0;   // Nombre de mauvaises réponses

    /**
     * Constructeur pour créer un nouveau joueur.
     *
     * @param name Le nom du joueur.
     */
    public Player (String name){
        this.name = name;
    }

    /*
     * Retourne le nom du joueur
     */
    public String getName(){
        return name;
    }

    /**
     * Incrémente le nombre de bonnes réponses du joueur.
     */
    public void bonneReponse(){
        BRep++; 
    }

    /*
     * Incrémente le nombre de mauvaises réponses du joueur.
     */
    public void mauvaiseReponse(){
        MRep++;
    }

    /*
     * Retourne le nombre de bonne réponse de ce joueur
     */
    public int getBonneReponse(){
        return BRep;
    }

    /**
     * Retourne une chaîne de caractères représentant les statistiques du joueur.
     * 
     * @return La chaîne de caractères contenant le nom du joueur et ses statistiques (nombre de bonne réponse, nb de mauvaise réponse, 
     * taux de bonne réponse en pourcentage).
     */
    public String getText(){
        return name + " " + BRep + " bonne réponses, " + MRep + " mauvaise réponses: " + (double)BRep / (BRep + MRep) * 100 + "%";
    }
}
