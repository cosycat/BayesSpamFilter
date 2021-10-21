import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        
        String spamLearnPath = "res/ham-anlern";
        String hamLearnPath = "res/spam-anlern";
        String spamCalibratePath = "res/spam-anlern";
        String hamCalibratePath = "res/spam-anlern";
        
        String mailPath = "";
    
        List<String> spamMails = getMailsAsStringList(spamLearnPath);
        List<String> hamMails = getMailsAsStringList(hamLearnPath);
    
        final var bsf = new BayesSpamFilter();
    
        bsf.learn(spamMails, hamMails);
    
        var spamCalibrateMails = getMailsAsStringList(spamCalibratePath);
        var hamCalibrateMails = getMailsAsStringList(hamCalibratePath);
    
        final var correctlyDetectedSpamCount = spamCalibrateMails.stream()
                .map(bsf::evaluate)
                .filter(result -> result == Result.SPAM)
                .count();
    
        final var correctlyDetectedHamCount = hamCalibrateMails.stream()
                .map(bsf::evaluate)
                .filter(result -> result == Result.HAM)
                .count();
    
        System.out.println("Found " + correctlyDetectedSpamCount + " SPAM emails out of " + spamCalibrateMails.size());
        System.out.println("Found " + correctlyDetectedHamCount + " HAM emails out of " + hamCalibrateMails.size());
    }
    
    
    
    private static List<String> getMailsAsStringList(final String spamLearnPath) {
        try {
            return Files.walk(Paths.get(spamLearnPath))
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            return Files.readString(path, StandardCharsets.ISO_8859_1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "";
                        }
                    })
                    .filter(s -> {
                        System.out.println(s);
                        return true;
                    })
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}



