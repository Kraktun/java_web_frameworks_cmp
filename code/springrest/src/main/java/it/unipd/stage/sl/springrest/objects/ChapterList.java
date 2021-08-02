package it.unipd.stage.sl.springrest.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

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
}
