import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BayesSpamFilter {
    
    private final double alpha = 0.00001;
    
    private final Pattern wordDelimiter = Pattern.compile("[\\W|\\s]+"); // Only check for natural human language words (more or less), ignore all the weird signs.
//    private final Pattern wordDelimiter = Pattern.compile("\\s+"); // Only use spaces (and newline etc.) as a delimiter, use all other signs as "words" to compare.
    
    private Map<String, BigDecimal> probabilitySpamMailContainingKeyWord;
    private Map<String, BigDecimal> probabilityHamMailContainingKeyWord;
    
    /**
     * Assigns each word a probability P(word|Spam) and P(word|Ham)
     *
     * @param spamMails List of Spam Mails
     * @param hamMails  List of Ham Mails
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
            
            // If we did not find a word at all in one kind (spam/ham), just ad a "count" of alpha as the number of found mails.
            // If we would not do that, afterwards when multiplying all probabilities of the words, if even one of them is 0,
            // the whole result would be 0 (as it's only multiplication (and division))
            if (spamWordCounter.containsKey(word)) {
                probabilitySpamMailContainingTheWord = spamWordCounter.get(word) * 1.0 / spamMails.size();
            } else {
                probabilitySpamMailContainingTheWord = alpha / spamMails.size();
            }
            probabilitySpamMailContainingKeyWord.put(word, BigDecimal.valueOf(probabilitySpamMailContainingTheWord));
            
            // Same for the other kind (spam/ham)
            if (hamWordCounter.containsKey(word)) {
                probabilityHamMailContainingTheWord = hamWordCounter.get(word) * 1.0 / hamMails.size();
            } else {
                probabilityHamMailContainingTheWord = alpha / hamMails.size();
            }
            probabilityHamMailContainingKeyWord.put(word, BigDecimal.valueOf(probabilityHamMailContainingTheWord));
        });
        System.out.println("DONE learning");
    }
    
    /**
     * Parses the given Mails (as Strings) and returns a Map containing each word which was at least in one Mail as keys
     * and the corresponding count of Mails which contain the word at least once as the corresponding values.
     *
     * @param mailList The list of mails.
     * @return A Map containing each word which was at least in one Mail as keys and the corresponding count of Mails which contain the word at least once as the corresponding values.
     */
    private Map<String, Integer> parseMailsForWords(final List<String> mailList) {
        Map<String, Integer> wordCounter = new HashMap<>();
        for (var mailText : mailList) {
            final var allWordsInMail = wordDelimiter.split(mailText);
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
    
    /**
     * Evaluates the given Mail, and returns the Result (either SPAM or HAM)
     * @param mail the mail to evaluate
     * @return Whether the Mail was categorized as Spam or Ham
     */
    public Result evaluate(final String mail) {
        var allWords = Arrays.stream(wordDelimiter.split(mail))
                .distinct()
                // Ignore words which never occurred in the learning phase.
                .filter(word -> probabilityHamMailContainingKeyWord.containsKey(word) && probabilitySpamMailContainingKeyWord.containsKey(word))
                .collect(Collectors.toUnmodifiableList());
        BigDecimal zaehler = new BigDecimal(1);
        for (String allWord : allWords) {
            BigDecimal v = probabilitySpamMailContainingKeyWord.get(allWord);
            zaehler = zaehler.multiply(v);
        }
        BigDecimal nenner = new BigDecimal(1);
        for (String word : allWords) {
            BigDecimal v = probabilityHamMailContainingKeyWord.get(word);
            nenner = nenner.multiply(v);
        }
        var Q = nenner.divide(zaehler, RoundingMode.HALF_UP).doubleValue();

//        System.out.println(Q);
        if (Q > 1)
            return Result.SPAM;
        else
            return Result.HAM;
    }
}

