package it.unipd.stage.sl.springrest.objects;

public interface ObjectUpdater<T> {

    void updateNonNull(T newObj);
}
