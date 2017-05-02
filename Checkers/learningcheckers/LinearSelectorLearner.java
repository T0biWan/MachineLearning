package learningcheckers;

import java.util.List;

public class LinearSelectorLearner {
    
    
    public LinearSelectorLearner() {}
    
    
    
    /**
     * Lernt bessere Parameter für einen LinearSelector.
     * Dabei werden Testspiele erzeugt und sowohl Spielstände sowohl mit dem teacher als auch
     * mit dem zu lernenden Selektor bewertet. Der zu lernende Selektor lernt also
     * wie der teacher zu spielen.
     *   
     * @param rounds Anzahl der Testspiele die maximal durchgeführt werden.
     * @param goalFraction Der Lernalgorithmus bricht ab, wenn der gelernte 
     *                     Selektor mindestens diesen Bruchteil an Testspielen
     *                     gegen den gegebenen base-Selektor gewinnt. 
     * @param base Der Selektor mit dessen Parametern der Lernvorgang beginnt
     *             und der als Vergleich zum Ermitteln des Lernerfolgs herangezogen wird.
     * @param teacher Der Selektor von dem gelernt werden soll.
     * @return Den gelernten Selektor.
     */
    public LinearSelector learnSupervised(int rounds, double goalFraction, LinearSelector base, LinearSelector teacher) {
        LinearSelector selector = (LinearSelector)base.clone();
        //TODO: Bitte Implementieren, also selector verbessern lassen
        return null;
    }
    
    
    /**
     * Lernt aus dem Spielverlauf, der durch li gegeben ist.
     * Verbessert den gegebenen Selector s, wobei s sich an den Bewertungen von teacher orientiert.
     */
    private void trainSupervised(LinearSelector s, List<Board> li, LinearSelector teacher) {
        //TODO: Bitte Implementieren!
    }
    
    
    
    /**
     * Lernt selbsständig bessere Parameter für einen LinearSelector.
     * Dabei werden Trainingsdaten automatisch über Testspiele erzeugt.
     *   
     * @param rounds Anzahl der Testspiele die maximal durchgeführt werden.
     * @param goalFraction Der Lernalgorithmus bricht ab, wenn der gelernte 
     *                     Selektor mindestens diesen Bruchteil an Testspielen
     *                     gegen den gegebenen base-Selektor gewinnt. 
     * @param base Der Selektor mit dessen Parametern der Lernvorgang beginnt
     *             und der als Vergleich zum Ermitteln des Lernerfolgs herangezogen wird.
     * @return Den gelernten Selektor.
     */
    public LinearSelector learnUnsupervised(int rounds, double goalFraction, LinearSelector base) {
        //TODO: Bitte Implementieren!
        return null;
    }
    
    
    /**
     * Lernt aus dem Spielverlauf, der durch li gegeben ist.
     * Verbessert den gegebenen Selector s. 
     */
    private void trainUnsupervised(LinearSelector s, List<Board> li) {
        //TODO: Bitte Implementieren!
    }
    
    
    /**
     * Führt numberOfGames Damespiele durch, wobei ein Spieler
     * seine Züge mittels base-Selektor ausführt und der andere
     * spieler mittels learned-Selector.
     * @return Den Bruchteil der Spiele, der von learned gewonnen wurde.
     */
    private double fractionOfGamesWon(LinearSelector base, LinearSelector learned, int numberOfGames) {
        // Gewonnen Spiele von learned
        int gamesWonByLearned = 0;

        // Spiele die nicht Unentschieden enden
        int gamesNotEven = 0;

        //Anzahl der gespielten Spiele
        int numberOfGamesPlayed = 0;

        while(numberOfGamesPlayed <= numberOfGames) {
            Game game = new Game(base, learned, false);
            int numberOfWinner = game.run(false);
            if(numberOfWinner == 1) gamesWonByLearned++;
            else if (numberOfWinner != -1) gamesNotEven++;
            numberOfGamesPlayed++;
        }

        return gamesWonByLearned/gamesNotEven;
    }
    
    

    
    public static void main(String[] args) {
        LinearSelectorLearner learner = new LinearSelectorLearner();
        HumanIntuitionLinearSelector human = new HumanIntuitionLinearSelector();

        LinearSelector base = new LinearSelector(8, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0);

        System.out.println(learner.fractionOfGamesWon(base, human, 100));
    }
}


