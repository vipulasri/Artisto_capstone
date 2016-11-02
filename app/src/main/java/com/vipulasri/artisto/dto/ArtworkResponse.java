package com.vipulasri.artisto.dto;

import com.vipulasri.artisto.model.Artwork;

import java.util.List;

/**
 * Created by HP-HP on 06-10-2016.
 */

public class ArtworkResponse {

    private long elapsedMilliseconds;
    private int count;
    private List<Artwork> artObjects;

    public long getElapsedMilliseconds() {
        return elapsedMilliseconds;
    }

    public void setElapsedMilliseconds(long elapsedMilliseconds) {
        this.elapsedMilliseconds = elapsedMilliseconds;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Artwork> getArtObjects() {
        return artObjects;
    }

    public void setArtObjects(List<Artwork> artObjects) {
        this.artObjects = artObjects;
    }
}
