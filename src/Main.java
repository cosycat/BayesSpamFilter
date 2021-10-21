import java.util.List;

public class Main {

    public static void main(String[] args) {
    
        String spamLearnPath = "";
        String hamLearnPath = "";
        
        String mailPath = "";
    
        List<String> spamMails = getMailsAsStringList(spamLearnPath);
        List<String> hamMails = getMailsAsStringList(hamLearnPath);
        
        final var bsf = new BayesSpamFilter();
    
        bsf.learn(spamMails, hamMails);
        bsf.evaluate(mailPath);
    
    }
    
    private static List<String> getMailsAsStringList(final String spamLearnPath) {
        return null;
    }
}



