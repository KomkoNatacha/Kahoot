package TPKahoot;

import java.util.TimerTask;

/*
 * Classe de compte à rebours pour la gestion du temps.
 * Cette classe étend TimerTask pour être exécutée comme une tâche.
 */
public class CountDown extends TimerTask {
        private int secondsLeft = 10;   // Le maximum du temps restant  est 10 secondes
        private Server server;          // Référence au serveur pour gérer les événements liés au temps
        private KahootServer ui;        // L'objet de l'interface utilisateur du serveur

        /**
         * Constructeur pour initialiser le compte à rebours.
         *
         * @param time      Le temps initial en secondes pour le compte à rebours.
         * @param server    L'objet du serveur pour gérer les événements liés au temps.
         * @param ui        L'objet de l'interface utilisateur du serveur.
         */
        public CountDown(int time, Server server, KahootServer ui){
            secondsLeft = time;
            this.server = server;
            this.ui = ui;
        }

        /*
         * Méthode exécutée à chaque tick du Timer.
         * Gère la décrémentation du temps et l'envoi des mises à jour au serveur et à l'interface utilisateur.
         */
        @Override
        public void run() {
            // Informe le serveur que le temps a diminué
            server.tickDown(secondsLeft);
            // Met à jour le temps affiché sur l'interface utilisateur
            ui.setTime(secondsLeft);

            if (secondsLeft <= 0) {
                // Arrête la tâche si le temps est écoulé
                cancel();
            }

            // Décrémente le compteur de secondes
            secondsLeft--;
        }
}
