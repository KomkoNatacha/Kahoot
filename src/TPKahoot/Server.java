package TPKahoot;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * Classe serveur pour l'application Kahoot.
 * Gère les connexions des clients, la diffusion des questions et la gestion du temps.
 */
public class Server implements Runnable{

	private ArrayList<Thread> connectionThread;	// Liste des threads pour chaque connexion client
	private ArrayList<Connection> connections;	// Liste des connexions client
	private int portNumber;						// Numéro de port du serveur
	private ServerSocket serverSocket;			// Socket du serveur pour accepter les connexions
	private boolean isRunning = false;			// Indique si le serveur est en cours d'exécution
	private KahootServer ui;					// Interface utilisateur du serveur
	
	/**
     * Le constructeur pour le serveur.
     *
     * @param portNumber Le numéro de port pour le serveur.
     * @param ui L'interface utilisateur du serveur Kahoot.
     */
	public Server(int portNumber, KahootServer ui) {
		this.ui = ui;
		connectionThread = new ArrayList<Thread>();
		connections = new ArrayList<Connection>();
		this.portNumber = portNumber;
	}

	/**
     * Méthode run pour l'exécution du serveur dans un thread.
     */
	public void run() {
		if(isRunning) {
			return;
		}
		isRunning = true;
		portNumber = 5555;
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server opened on port " + portNumber);

            this.serverSocket = serverSocket;
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouvelle connection de: " + clientSocket.getInetAddress());

                // Gère chaque client dans un thread indépendant
                Connection connection = new Connection(clientSocket, ui);
                Thread connectionThread = new Thread(connection);
                connectionThread.start();
                this.connectionThread.add(connectionThread);
                this.connections.add(connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
     * Envoie les questions à tous les clients connectés.
     *
     * @param questions Les questions à envoyer.
     */
	public void sendQuestion(String[] questions) {
		for (Connection connection : connections) {
			connection.sendData(questions);
		}
	}

	/**
     * Envoie le message a tous les joureurs avec le joueur ayant ganger la partie.
     *
     * @param message Le message qui sera afficher a tous le joueurs.
     */
	public void sendWinner(String message){
				for (Connection connection : connections) {
			connection.sendData(message);
		}
	}

	/**
     * Envoie une mise à jour du temps restant à tous les clients.
     *
     * @param time Le temps restant.
     */
	public void tickDown(int time){
		for (Connection connection : connections){
			connection.sendData("T:" + time);
		}
	}
	
	/*
     * Ferme le serveur et toutes les connexions client.
     */
	public void closeServer() {
		try {
			serverSocket.close();
			for (Thread clientThread : connectionThread) {
				clientThread.interrupt();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Le serveur a été fermé");
	}
	
	/**
     * Vérifie si le serveur est toujours en cours d'exécution.
     * 
     * @return Vrai si le serveur est en cours d'exécution, faux sinon.
     */
	public boolean stillRunning() {return isRunning;}
}
