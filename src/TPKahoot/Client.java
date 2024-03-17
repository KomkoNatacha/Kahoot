package TPKahoot;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JOptionPane;


public class Client implements Runnable{
	
    private Socket clientSocket;
    private PrintWriter out;
    private Scanner in;
	KahootClient ui;

	/**
	* Le constructeur de Client, qui permet de créer la connection avec le serveur,
	* et envoyer le nom du client.
	* @param: ip	L'adresse IP de serveur
	* @param: port	Le numéro du port de côté serveur
	* @param: ui	L'objet d'interface utilisateur de côté client
	*/
    public Client(String ip, int port, KahootClient ui) {
		this.ui = ui;
        try{
	        clientSocket = new Socket(ip, port);
	        out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
			String nom = JOptionPane.showInputDialog(null, "Entrez votre nom:");
			// Envoyer le nom du client au serveur
			sendData("NC:" + nom);
        }
        catch (Exception e) {
			e.printStackTrace();
		}
    }

	/**
	* La méthode qui permet d'envoyer les données du client au serveur,
	* 'NC' signifie 'New Client'.
	*/ 	
    public void sendData(String msg) {
        out.println(msg);
    }

	/**
	 * Cette méthode permet de vérifier continuellement les entrées du serveur et de répondre selon 
	 * les différents types d'entrées.
	 */
	public void run() {
		// Une boucle infinie en surveillant continuellement les données du serveur
		while(true) { 
			// S'il y a des nouvelles données du serveur
	        if (in.hasNextLine()) {
				System.out.println("Incomming data");
				// Lire une ligne des données
	            String serverData = in.nextLine();

				// Si l'entrée est commencé par NQ, signifie 'New Question'
				if (serverData.startsWith("NQ:")){
					serverData = serverData.substring(3).trim();
					String[] data = serverData.split("/");
					// Extraire les options de question
					String[] choix = {data[1], data[2], data[3], data[4]};
					// Afficher la questions et les options dans l'interface de client
					ui.incommingQuestion(data[0], choix);
				}
				// Si l'entrée est commencé par VAL, signifie 'Value'
				if (serverData.startsWith("VAL:")){
					// Supprimer l'identificateur du type de message
	            	String rep = serverData.substring(4).trim();
					// Valider si la réponse soit correct
					ui.incommingValidation(rep);
				}
				// Si l'entrée est commencé par T, signifie 'Time'
				if (serverData.startsWith("T:")){
					// Supprimer l'identificateur du type de message
					String time = serverData.substring(2).trim();
					// Update le temps restant
					ui.updateTime(time);
				}
				// Si l'entrée est commencé par un message donnant le joueur gangant
				if (serverData.startsWith("Le")){
					JOptionPane.showMessageDialog(null, serverData);
				}
	        }
		}
		
	}

	/*
	 * Fermer la connection de côté client.
	 * Cette méthode permet de fermer le flow d'entrée et de sortie, et aussi 
	 * fermer le socket du côté client.
	 */
    public void stopConnection() {
		in.close();
        out.close();
        try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
