import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        
        String spamLearnPath = "res/ham-anlern";
        String hamLearnPath = "res/spam-anlern";
//        String spamCalibratePath = spamLearnPath;
//        String hamCalibratePath = hamLearnPath;
        String spamCalibratePath = "res/spam-kallibrierung";
        String hamCalibratePath = "res/ham-kallibrierung";
        
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
                            return Files.readString(path, Charset.defaultCharset());
                        } catch (IOException e) {
                            return "";
                        }
                    })
                    .filter(s -> {
//                        System.out.println(s);
                        return s.length() >= 1; // filter out empty files and failed files
                    })
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}



