import java.io.BufferedOutputStream;
import java.io.PrintStream;

/**
 * @author zoujl
 * @create 2023-03-11-16:21
 */
public class run {
    private static final PrintStream outStream = new PrintStream(new BufferedOutputStream(System.out));
    public static void main(String[] args) {
        int i=0;
        while(true){
            outStream.printf("%d\n", i);
            i++;
        }
    }
}
