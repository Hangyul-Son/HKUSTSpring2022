package base.lab4;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Folder implements Comparable<Folder>, Serializable {

    ArrayList<Note> notes;
    String name;

    Folder(String name) {
        this.name = name;
        notes = new ArrayList<Note>();
    }

    public void addNote(Note note){
        notes.add(note);
    }
    public String getName() {
        return name;
    }
    public ArrayList<Note> getNotes(){
        return notes;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return name.equals(folder.name);
    }

    public String toString() {
        int nText = 0;
        int nImage = 0;
        for(Note n : notes){
            if(n instanceof TextNote){ nText++; }
            else if(n instanceof ImageNote){ nImage++;}
        }
        return name + ":" + nText + ":" + nImage;
    }

    @Override
    public int compareTo(Folder o) {
        return this.name.compareTo(o.name);
    }

    public void sortNotes() {
        Collections.sort(notes);
    }

    public List<Note> searchNotes(String keyword) {
        List<Note> selectedNotes = new ArrayList<>();
        String[] keywords = keyword.split(" ");
        for(Note note: notes){
            Boolean isSelected = true;
            Boolean[] index = new Boolean[keywords.length];
            Arrays.fill(index, false);
            for(int i =0; i<keywords.length; i++) {
                String word = keywords[i];
                if (word.equals("OR") || word.equals("or")) {
                    if (note instanceof TextNote) {
                        if (note.getTitle().toLowerCase().contains(keywords[i - 1].toLowerCase()) ||
                                note.getTitle().toLowerCase().contains(keywords[i + 1].toLowerCase()) ||
                                ((TextNote) note).content.toLowerCase().contains(keywords[i + 1].toLowerCase()) ||
                                ((TextNote) note).content.toLowerCase().contains(keywords[i - 1].toLowerCase())) {
                            index[i + 1] = true;
                            index[i - 1] = true;
                            index[i] = true;
                        }
                        else {
                            isSelected = false;
                            continue;
                        }
                    } else {
                        if (note.getTitle().toLowerCase().contains(keywords[i - 1].toLowerCase()) ||
                                note.getTitle().toLowerCase().contains(keywords[i + 1].toLowerCase())) {
                            index[i + 1] = true;
                            index[i - 1] = true;
                            index[i] = true;
                        }
                        else{
                            isSelected = false;
                            continue;
                        }
                    }
                }
            }
            if(isSelected.equals(false)){
                continue;
            }
            for(int i=0; i<keywords.length; i++){
                if(index[i].equals(false)){
                    isSelected = note.getTitle().toLowerCase().contains(keywords[i].toLowerCase());
                }
            }
            if(isSelected.equals(true)) {
                selectedNotes.add(note);
            }
        }
        return selectedNotes;
    }
}
