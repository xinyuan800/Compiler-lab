import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main
{    
    public static void main(String[] args) throws IOException {
        String filePath = args[0];
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = "";
        while((line = reader.readLine())!=null){
            System.out.println(line);
        }

    }
}