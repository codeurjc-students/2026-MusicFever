import { Image } from "./image.model";

export interface Artist{
    id: number;
    name: string;
    smallImage: Image;
}

export interface ArtistDisplay{
    id: number;
    name: string;
    bigImage: Image;
    genres: string[];
    listeners: number;
}