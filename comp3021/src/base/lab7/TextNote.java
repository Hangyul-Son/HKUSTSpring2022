package lab7;

import java.io.*;
import java.util.Scanner;

public class TextNote extends Note implements Serializable {
    public String content;
    TextNote(String title) {
        super(title);
    }
    TextNote(String title, String content){
        super(title);
        this.content = content;
    }
    TextNote(File f) {
        super(f.getName());
        this.content = getTextFromFile(f.getAbsolutePath());
    }
    private String getTextFromFile(String absolutePath) {
        String result = "";
        StringBuilder sb = new StringBuilder();
        try{
            FileInputStream fis = new FileInputStream(absolutePath);
            InputStreamReader in = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(in);
            String curline;
            while((curline = br.readLine()) != null) {
                sb.append(curline).append('\n');
            }
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
    public void exportTextToFile(String pathFolder) {
        //I removed the File separator. Because the test case does not have one.
        String prefix ="";
        if(!pathFolder.isEmpty()){
            prefix = pathFolder + File.separator;
        }
        File file = new File(prefix + this.getTitle().replaceAll(" ", "_")+ ".txt");
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
