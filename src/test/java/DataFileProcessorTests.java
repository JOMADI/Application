import com.mytest.DataFileProcessor;
import junit.framework.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFileProcessorTests {


    @Test
    public void matcherResultMustNotBeNull(){
        String testString = "D Susan Holland,SAN FRANCISCO,04810023X";
        Pattern testRegex = Pattern.compile("[D\\w]([^ ][^,]*)");
        Matcher matcher = testRegex.matcher(testString);
        String[] testOutput = new String[3];
        int counter = 0;
        while (matcher.find()){
            testOutput[counter++] = matcher.group(0);
        }

        DataFileProcessor fileProcessor = new DataFileProcessor();
        String[] data = fileProcessor.matchDataPattern(testRegex, testString);

        Assert.assertEquals(testOutput[0], data[0]);
        Assert.assertEquals(testOutput[1], data[1]);
        Assert.assertEquals(testOutput[2], data[2]);
    }
}
