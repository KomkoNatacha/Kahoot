package TPKahoot;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/*
 * Traiter la connection de chaque client.
 * Cette classe implémente Runnable pour permettre de le lancer dans
 * un thread indépendant.
 */
public class Connection implements Runnable{
	
    PrintWriter out;
    Scanner in;
	Player player;
	KahootServer ui;
    
    /**
	 * Le constructeur de Connection
	 * 
	 * @param clientSocket	Le socket du client
	 * @param ui		L'objet d'interface d'utilisateur de client
	 */
	public Connection(Socket clientSocket, KahootServer ui) {
		this.ui = ui;
        try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));   
		} catch (IOException e) {
			e.printStackTrace();
		}       

	}
	
	/*
	 * Cette méthode permet de surveiller et traiter les données envoyées par serveur
	 */
	public void run() {	
		while (true) {
		        String incomming;
				if (in.hasNextLine()) {
					incomming = in.nextLine();
					// L'entrée commencée par 'NC' signifie 'New Client'
					if (incomming.startsWith("NC:")){
						// Ajouter un joueur
						ui.addPlayer(incomming.substring(3).trim(), this);
					}
					// L'entrée commencée par 'RP' signifie 'Respond'
					if (incomming.startsWith("RP:")){
						ui.gotAnswer(incomming.substring(3).trim(), this);
					}
					// L'entrée commencée par 'TO' signifie 'Time Out'
					if (incomming.startsWith("TO")){
						// On considère que le joueur donne une mauvaise réponse dans ce cas
						player.mauvaiseReponse();
						// Update l'ui
						ui.refresh();
					}
				}
		}
    }
	
	/**
	 * Envoyer les données à client
	 * 
	 * @param msg: Les données à envoyer
	 */
	public void sendData(String msg) {
		this.out.println(msg);
	}

	/**
	 * Envoyer les données qui contiennent les questions
	 * 
	 * @param msg: La liste qui contient la question et les choix de cette question
	 */
	public void sendData(String[] msg){
		this.out.println("NQ:" + msg[0] + "/" + msg[1]);
	}

	/**
	 * Définir le joueur qui est associé à cette connection.
	 * 
	 * @param player: L'objet de Player
	 */
	public void setPlayer(Player player){
		this.player = player;
	}

	/**
	 * Retourner le joueur qui est associé à cette connection
	 * 
	 * @param player: L'objet de Player
	 */
	public Player getPlayer(){
		return player;
	}
	
}
