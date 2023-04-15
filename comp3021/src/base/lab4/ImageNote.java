package base.lab4;

import java.io.File;
import java.io.Serializable;

public class ImageNote extends Note {
    public File image;
    ImageNote(String title) {
        super(title);
    }
}
