package TPKahoot;

import javax.swing.JOptionPane;

/**
 * Classe principale pour Kahoot.
 * Permet aux utilisateurs de choisir entre démarrer un client ou un serveur.
 */
public class Kahoot {

	/**
     * Point d'entrée principal de l'application.
     * 
     * @param args Arguments de la ligne de commande.
     */
	public static void main(String[] args) {
		String[] options = {"Client", "Serveur"};	// Options pour le choix entre client et serveur
		// Affiche une boîte de dialogue pour choisir entre client et serveur
		int choisie = JOptionPane.showOptionDialog(null, "Choisissez serveur ou client", null, 0, 0, null, options, args);
		
		if (choisie == 0) {
			// Lance le côté client si l'option choisie est "Client"
			CreatClient();
		}
		else {
			// Sinon, lance le serveur
			CreatServer();
		}
		
	}
	
	/**
     * Crée et lance une instance de client Kahoot.
     */
	static void CreatClient() {
		KahootClient client = new KahootClient();
		
	}
	
	/*
     * Crée et lance une instance de serveur.
     */
	static void CreatServer() {
		KahootServer serverWindow = new KahootServer();
	}
}
