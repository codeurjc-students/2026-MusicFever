package com.musicfever.music_fever.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String artistName;
    private int number;
    private boolean explicit;

    public Track(){}
    
    public Track(String artistName, String name, int number, boolean explicit) {
        this.artistName = artistName;
        this.name = name;
        this.number = number;
        this.explicit = explicit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Track other = (Track) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (artistName == null) {
            if (other.artistName != null)
                return false;
        } else if (!artistName.equals(other.artistName))
            return false;
        if (number != other.number)
            return false;
        if (explicit != other.explicit)
            return false;
        return true;
    }    
}
