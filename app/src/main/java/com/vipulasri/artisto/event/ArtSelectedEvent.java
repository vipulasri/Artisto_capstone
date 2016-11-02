package com.vipulasri.artisto.event;

import com.vipulasri.artisto.model.Artwork;

/**
 * Created by HP-HP on 16-10-2016.
 */
public class ArtSelectedEvent {

    public final Artwork artwork;

    public ArtSelectedEvent(Artwork artwork) {
        this.artwork = artwork;
    }

    public Artwork getSelectedArt() {
        return artwork;
    }

}
