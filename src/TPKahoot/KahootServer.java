package TPKahoot;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

/*
* Classe principale du serveur pour l'application Kahoot, qui permet de
* gérer l'interface utilisateur du serveur, la logique de jeu, et les connexions avec les client.
*/
public class KahootServer extends JFrame {
    private Server server;
    private Thread serverThread;            // Thread pour exécuter le serveur
    private static int portNumber = 5555;
    private int questionNumber = 0;         // Numéro de la question actuelle
    private ArrayList<String[]> Questions;  // Liste des questions du jeu
    private ArrayList<Player> Players;      // Liste des joueurs connectés
    private JTextArea participant;          // Zone de texte pour afficher les joueurs
    private JTextArea ongoingQuestion;      // Zone de texte pour afficher la question en cours
    private int[] statsRep;                 // Tableau pour les statistiques de réponses
    private String[] options;               // Options de réponse pour la question actuelle

    /*
     * Le constructeur pour KahootServer.
     * Initialise l'interface utilisateur et démarre le serveur.
     */
    public KahootServer() {
        setSize(450, 300);

        server = new Server(portNumber, this);
        // Prépare et démarre le thread du serveur
        serverThread = new Thread(server);
        serverThread.start();

        Players = new ArrayList<Player>();

        //Création et configuration des composants de l'interface utilisateur
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        ongoingQuestion = new JTextArea("Question:");
        participant = new JTextArea("Players:");
        participant.setEditable(false);  // Make it non-editable

        JButton button1 = new JButton("Prochaine Question");
        JButton button2 = new JButton("Recommancer");
        JButton button3 = new JButton("Terminer Kahoot");

        buttonPanel.add(button1);

        // Définit les actions à effectuer lorsque les boutons sont cliqués
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendNextQuestion();
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                questionNumber = 0;
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.closeServer();
                System.exit(0);
            }
        });


        buttonPanel.add(button2);
        buttonPanel.add(button3);

        // Utilise un JScrollPane pour le JTextArea
        add(new JScrollPane(participant), BorderLayout.CENTER);  

        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        bottomPanel.add(ongoingQuestion, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Lire le fichier de questions (Question.txt) et initialiser la liste des questions
        try {
            Questions = readQuestionFile("src/TPKahoot/Questions.txt");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    /*
     * Envoie la prochaine question aux clients et met à jour l'interface.
     * Gère également le timer pour chaque question
     */
    private void sendNextQuestion() {
        // Vérifie s'il reste des questions à poser
        if (questionNumber >= Questions.size()){
            JOptionPane.showMessageDialog(null, "Aucune autre question dans le fichier de question, clicker sur Recommancer pour rejouer");
            return;
        }
        // Obtient les options de la question actuelle
        options = Questions.get(questionNumber)[1].split("/");
        // Réinitialise les statistiques de réponses
        statsRep = new int[]{0,0,0,0};
        // Met à jour le texte de la question en cours
        ongoingQuestion.setText("Question: " + Questions.get(questionNumber)[0] + " 10s");
        // Prépare et envoie la question aux clients
        String[] send = {Questions.get(questionNumber)[0], Questions.get(questionNumber)[1]};
        server.sendQuestion(send);
        questionNumber++;
        // Crée un nouveau timer
        Timer timer = new Timer();

        // Démarre le timer à rebours pour la question
        timer.scheduleAtFixedRate(new CountDown(10, server, this), 0, 1000);   
    }

    /**
    * Met à jour le temps restant pour la question actuelle.
    * Détermine aussi le gagnant
    * @param time Le temps restant en secondes.
    */
    public void setTime(int time){
        // Si le temps est écoulé, affiche les statistiques des réponses
        if (time == 0){
            String reponse = "Les joueurs ont répondu: \n";
            reponse += options[0] + ": " + statsRep[0] + "\n";
            reponse += options[1] + ": " + statsRep[1] + "\n";
            reponse += options[2] + ": " + statsRep[2] + "\n";
            reponse += options[3] + ": " + statsRep[3] + "\n";
            JOptionPane.showMessageDialog(null, reponse);
            ongoingQuestion.setText("Question: " + Questions.get(questionNumber - 1)[0] + " (temps écoulé)");

            //Si nous sommes a la fin du questionnaire
            if (questionNumber == Questions.size()){
                int max = 0;
                ArrayList<Player> gagnants = new ArrayList<Player>();
                //For pour faire la liste des joueur(s) avec le plus de bonne réponses
                for (int i=0; i < Players.size(); i++){
                    if (Players.get(i).getBonneReponse() > max){
                        gagnants = new ArrayList<Player>();
                        gagnants.add(Players.get(i));
                        max = Players.get(i).getBonneReponse();
                    }
                    else if (Players.get(i).getBonneReponse() == max){
                        gagnants.add(Players.get(i));
                    }
                }

                //Construit le message qui sera affiché a tous
                String message;
                if (gagnants.size() == 0)
                    message = "Le joueur ayant eu le plus de bonne réponses est:";
                else
                    message = "Les joueurs ayant eu le plus de bonne réponses sont:";
                for (int i = 0; i < gagnants.size(); i++){
                    if (i != 0)
                        message += " & ";
                    else
                        message += " ";
                    message += gagnants.get(i).getName();
                }

                //Envoie le vainqueur a tous les joueurs
                server.sendWinner(message);
                JOptionPane.showMessageDialog(null, message);
            }
            return;
        }

        // Met à jour le texte de la question avec le temps restant
        ongoingQuestion.setText("Question: " + Questions.get(questionNumber - 1)[0] + " " + time + "s");
    }

     /**
     * Ajoute un nouveau joueur à la partie et met à jour l'interface.
     *
     * @param name Le nom du joueur.
     * @param connection La connexion associée au joueur.
     */
    public void addPlayer(String name, Connection connection) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Player newPlayer = new Player(name);
                connection.setPlayer(newPlayer);
                Players.add(newPlayer);
                participant.append("\n" + name);  // Use append for JTextArea
            }
        });
    }

    /**
     * Traite la réponse reçue d'un joueur.
     *
     * @param answer        La réponse du joueur.
     * @param connection    La connexion associée au joueur.
     */
    public void gotAnswer(String answer, Connection connection) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Incrémente le compteur de la réponse correspondante
                if (answer.equals(options[0]))
                    statsRep[0]++;
                if (answer.equals(options[1]))
                    statsRep[1]++;
                if (answer.equals(options[2]))
                    statsRep[2]++;
                if (answer.equals(options[3]))
                    statsRep[3]++;
                // Envoie une validation à la connexion et met à jour le score du joueur
                if (answer.equals(Questions.get(questionNumber - 1)[2])){
                    connection.sendData("VAL:" + "1");
                    connection.getPlayer().bonneReponse();
                }
                else{
                    connection.sendData("VAL:" + "0");
                    connection.getPlayer().mauvaiseReponse();
                }
                refresh();
            }
        });
        
    }

    /**
     * Lit et charge les questions à partir d'un fichier.
     *
     * @param filePath Chemin du fichier contenant les questions.
     * @return ArrayList<String[]> Liste des questions avec leurs options et réponses.
     * @throws IOException En cas d'erreur de lecture du fichier.
     */
    private static ArrayList<String[]> readQuestionFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] currentQuestion = null;
            ArrayList<String[]> list = new ArrayList<String[]>();

            // Lire le fichier Question.txt et extraire les questions
            while ((line = reader.readLine()) != null) {
                /**
                 * Détecte et traite chaque partie d'une question
                 * Si la ligne commence par 'Q': Question
                 * Si la ligne commence par 'C': Choix
                 * Si la ligne commence par 'R': Réponse correcte
                 */ 
                if (line.startsWith("Q:")) {
                    currentQuestion = new String[3];
                    currentQuestion[0] = line.substring(2).trim();
                } else if (line.startsWith("C:")) {
                    currentQuestion[1] = line.substring(2).trim();
                } else if (line.startsWith("R:")) {
                    currentQuestion[2] = line.substring(2).trim();
                    list.add(currentQuestion);
                }
            }

            return list;
        }
    }

    /*
     * Rafraîchit l'interface pour afficher les informations des joueurs.
     */
    public void refresh(){
        String text = "Players:";
        for (int i = 0; i < Players.size(); i++){
            // Ajoute les informations de chaque joueur
            text += "\n" + Players.get(i).getText();
        }
        // Met à jour la zone de texte des participants
        participant.setText(text);
    }
}
