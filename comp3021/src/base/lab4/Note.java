package base.lab4;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Note implements Comparable<Note>, Serializable {
    private Date date;
    private String title;

    Note(String title) {
        this.title = title;
        this.date = new Date(); //contructor assigns System.currentTimeMillis() automatically
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;
        Note note = (Note) o;
        return Objects.equals(this.title, note.title);
    }
    public String getTitle() {
        return this.title;
    }

    @Override
    public int compareTo(Note o) {
        return this.date.compareTo(o.date);
    }

    @Override
    public String toString() {
        return date.toString() + "\t" +title;
    }
}
