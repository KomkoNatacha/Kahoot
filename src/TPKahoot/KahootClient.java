package TPKahoot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Classe client pour l'application Kahoot.
 * Fournit l'interface utilisateur pour le client et gère la communication avec le serveur.
 */
public class KahootClient extends JFrame {
    private Client client;
    private Thread clientThread;    // Thread pour exécuter le client
    private JLabel questionLabel;   // Étiquette pour afficher la question
    private JButton optionAButton;  //Boutton pour chaque option (A, B, C, D)
    private JButton optionBButton;
    private JButton optionCButton;
    private JButton optionDButton;
    private JLabel timerLabel;      // Étiquette pour afficher le temps restant
    private JPanel gamePanel;       // Panneau pour le jeu
    private JLabel stateLabel;      // Étiquette pour afficher l'état du score
    private int scoreGood = 0;      // Score pour les bonnes réponses
    private int scoreBad = 0;       // Score pour les mauvaises réponses

    /*
     * Le constructeur pour KahootClient.
     * Initialise l'interface utilisateur et démarre la connexion client.
     */
    public KahootClient() {
        // Demande l'adresse IP du serveur
        String ip = JOptionPane.showInputDialog(null, "Entrez l'adresse ip du destinataire", "127.0.0.1");
    	
        // Initialise et démarre le client
        client = new Client(ip, 5555, this);
        clientThread = new Thread(client);
        clientThread.start();

        // Configuration de l'interface utilisateur de client
    	JPanel statePanel = new JPanel(new BorderLayout());
    	stateLabel = new JLabel("Score: en attente d'une première question");
    	
    	statePanel.setPreferredSize(new Dimension(this.getWidth(),200));
    	
    	statePanel.add(stateLabel,BorderLayout.CENTER);
        
        questionLabel = new JLabel("Question");
        
        // Initialisation des boutons pour les options de réponse
        optionAButton = new JButton("OptionA");
        optionAButton.setBackground(Color.RED);

        var obj = this;

        // Ajout d'ActionListener pour chaque bouton
        optionAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("RP:" + optionAButton.getText());
                obj.setSize(800,200);
                gamePanel.setVisible(false);
            }
        });
        
        optionBButton = new JButton("OptionB");
        optionBButton.setBackground(Color.GREEN);

        optionBButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("RP:" + optionBButton.getText());
                obj.setSize(800,200);
                gamePanel.setVisible(false);
            }
        });
        
        optionCButton = new JButton("OptionC");
        optionCButton.setBackground(Color.BLUE);

        optionCButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("RP:" + optionCButton.getText());
                obj.setSize(800,200);
                gamePanel.setVisible(false);
            }
        });
        
        optionDButton = new JButton("OptionD");
        optionDButton.setBackground(Color.YELLOW);

        optionDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendData("RP:" + optionDButton.getText());
                obj.setSize(800,200);
                gamePanel.setVisible(false);
            }
        });
        
        timerLabel = new JLabel("Time: 0");
        
        gamePanel = new JPanel(new BorderLayout());

        // Configuration du layout du jeu
        JPanel questionPanel = new JPanel(new GridLayout(1, 1));
        questionPanel.add(questionLabel);
        gamePanel.add(questionPanel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 2, 2));
        optionsPanel.add(optionAButton);
        optionsPanel.add(optionBButton);
        optionsPanel.add(optionCButton);
        optionsPanel.add(optionDButton);
        gamePanel.add(optionsPanel, BorderLayout.CENTER);

        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerPanel.add(timerLabel);
        gamePanel.add(timerPanel, BorderLayout.SOUTH);
        
        this.setLayout(new BorderLayout());
        this.add(statePanel,BorderLayout.NORTH);
        this.add(gamePanel,BorderLayout.CENTER);
        gamePanel.setVisible(false);
      

        // Configuration de la fenêtre
        this.setTitle("Client UI");
        this.setSize(800, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * Gère l'affichage d'une nouvelle question.
     * 
     * @param question  La question à afficher.
     * @param choix     Les options de réponse pour la question
     */
    public void incommingQuestion(String question, String[] choix){
        var obj = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Mise à jour des textes des boutons et de la question
                optionAButton.setText(choix[0]);
                optionBButton.setText(choix[1]);
                optionCButton.setText(choix[2]);
                optionDButton.setText(choix[3]);

                questionLabel.setText("Question: " + question);
                obj.setSize(800, 600);
                gamePanel.setVisible(true);            
                }
        });
    }

    /**
     * Gère la validation de la réponse du joueur.
     * @param validation Le résultat de la validation ("1" pour correct, "0" pour incorrect).
     */
    public void incommingValidation(String validation){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Affichage du résultat et mise à jour du score selon la validation
                if (validation.equals("1")){
                    
                    JOptionPane.showMessageDialog(null, "Bonne reponse");
                    scoreGood++;
                }
                else{
                    JOptionPane.showMessageDialog(null, "Mauvaise reponse");
                    scoreBad++;
                }
                stateLabel.setText("Score: " + scoreGood + " Réussie, " + scoreBad + " Ratée (" + (double)scoreGood / (scoreGood + scoreBad) * 100 + "%)");
            }
        });
    }

    /**
     * Met à jour l'affichage du temps restant pour la question en cours
     * 
     * @param time Le temps restant.
     */
    public void updateTime(String time){
        var obj = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!gamePanel.isVisible())
                    return;
                // Gestion de l'affichage du temps et des états de fin de temps
                if (time.equals("0")){
                    if (gamePanel.isVisible()){
                        obj.setSize(800, 200);
                        gamePanel.setVisible(false);
                        JOptionPane.showMessageDialog(null, "Temps écoulé");
                        client.sendData("TO");
                        scoreBad++;
                        return;
                    }
                }

                timerLabel.setText("Temps restant: " + time);
                obj.setSize(800, 600);
                gamePanel.setVisible(true);            
                }
        });
    }
}