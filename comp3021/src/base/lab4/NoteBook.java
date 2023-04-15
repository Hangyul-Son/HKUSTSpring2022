package base.lab4;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NoteBook implements Serializable {
    private static final long serialVersionUID = 1L;

    ArrayList<Folder> folders;
    NoteBook() {
        folders = new ArrayList<Folder>();
    }
    /** *
     * Constructor of an object NoteBook from an object serialization on disk *
     * @param file, the path of the file for loading the object serialization */
    NoteBook(String file){
        try{
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fis);
            NoteBook n = (NoteBook) in.readObject(); //Downcasting. Should be returned as the god object which is Object class.
            in.close();
            folders = n.folders;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean createImageNote(String folderName, String title){
        ImageNote imageNote = new ImageNote(title);
        return insertNote(folderName, imageNote);
    }
    public boolean createTextNote(String folderName, String title){
        TextNote textNote = new TextNote(title);
        return insertNote(folderName, textNote);
    }
    public boolean createTextNote(String folderName, String title, String content){
        TextNote textNote = new TextNote(title, content);
        return insertNote(folderName, textNote);

    }
    public ArrayList<Folder> getFolders(){
        return folders;
    }

    public boolean insertNote(String folderName, Note note){
        //The fact that the folder has been made as an array is not clever.
        for(Folder folder: folders){
            if(folder.getName().equals(folderName)){
                for(Note noteTemp: folder.notes) {
                    if(noteTemp.equals(note)){
                        System.out.println("Creating note "+ note.getTitle() + " under folder " + folderName + " failed");
                        return false;
                    }
                }
                folder.addNote(note);
                return true;
            }
        }
        Folder folder = new Folder(folderName);
        folder.notes.add(note);
        folders.add(folder);
        return true;
    }

    public void sortFolders() {
        for(int i=0; i< folders.size(); i++){
            folders.get(i).sortNotes();
        }
        Collections.sort(folders);
    }
    public List<Note> searchNotes(String keyword) {
        List<Note> selectedNotes = new ArrayList<>();
        for(Folder folder: folders){
            selectedNotes.addAll(folder.searchNotes(keyword));
        }
        return selectedNotes;
    }
    public boolean save(String file){
        try{
            FileOutputStream pos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(pos);
            out.writeObject(this);
            out.close();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}

