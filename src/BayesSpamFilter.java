import java.util.*;

public class BayesSpamFilter {
    
    private final double alpha = 0.1;
    
    private Map<String, Double> probabilitySpamMailContainingKeyWord;
    private Map<String, Double> probabilityHamMailContainingKeyWord;
    
    /**
     * Assigns each word a probability P(word|Spam) and P(word|Ham)
     * @param spamMails List of Spam Mails
     * @param hamMails List of Ham Mails
     */
    public void learn(final List<String> spamMails, final List<String> hamMails) {
        // Set them new, to not mix up with previous learnings.
        probabilitySpamMailContainingKeyWord = new HashMap<>();
        probabilityHamMailContainingKeyWord = new HashMap<>();
        
        var spamWordCounter = parseMailsForWords(spamMails);
        var hamWordCounter = parseMailsForWords(hamMails);
        
        Set<String> allWords = new HashSet<>();
        allWords.addAll(spamWordCounter.keySet());
        allWords.addAll(hamWordCounter.keySet());
        
        allWords.forEach(word -> {
            double probabilitySpamMailContainingTheWord;
            double probabilityHamMailContainingTheWord;
            if (spamWordCounter.containsKey(word)) {
                probabilitySpamMailContainingTheWord = spamWordCounter.get(word) * 1.0 / spamMails.size();
            } else {
                probabilitySpamMailContainingTheWord = alpha / spamMails.size();
            }
            probabilitySpamMailContainingKeyWord.put(word, probabilitySpamMailContainingTheWord);
    
            if (hamWordCounter.containsKey(word)) {
                probabilityHamMailContainingTheWord = hamWordCounter.get(word) * 1.0 / hamMails.size();
            } else {
                probabilityHamMailContainingTheWord = alpha / hamMails.size();
            }
            probabilityHamMailContainingKeyWord.put(word, probabilityHamMailContainingTheWord);
        });
    }
    
    /**
     * Parses the given Mails (as Strings) and returns a Map containing each word which was at least in one Mail as keys
     * and the corresponding count of Mails which contain the word at least once as the corresponding values.
     * @param mailList The list of mails.
     * @return A Map containing each word which was at least in one Mail as keys and the corresponding count of Mails which contain the word at least once as the corresponding values.
     */
    private Map<String, Integer> parseMailsForWords(final List<String> mailList) {
        Map<String, Integer> wordCounter = new HashMap<>();
        for (var mailText : mailList) {
            final var allWordsInMail = mailText.split(" ");
            Arrays.stream(allWordsInMail).distinct().forEach(word -> {
                if (wordCounter.containsKey(word)) {
                    wordCounter.put(word, wordCounter.get(word) + 1);
                } else {
                    wordCounter.put(word, 1);
                }
            });
        }
        return wordCounter;
    }
    
    
    
    public void evaluate(final String mail) {
    
    }
}
