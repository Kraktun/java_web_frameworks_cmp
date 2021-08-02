package it.unipd.stage.sl.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Chapters as a list without events for the getAll method
 */
public class ChapterList {

    @JsonIgnoreProperties({"events", "starter"})
    List<Chapter> chapters;

    public ChapterList() {
    }

    public ChapterList(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    @Override
    public String toString() {
        String chaptersString = chapters == null? "" : chapters.stream().map(Chapter::toString).collect(Collectors.joining(", "));
        return "ChapterList{" +
                "chapters=[" + chaptersString +
                "]}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChapterList that = (ChapterList) o;
        return Objects.equals(chapters, that.chapters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chapters);
    }
}
