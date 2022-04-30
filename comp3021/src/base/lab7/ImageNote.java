package lab7;

import java.io.File;
import java.io.Serializable;

public class ImageNote extends Note implements Serializable {
    public File image;
    ImageNote(String title) {
        super(title);
    }
}
