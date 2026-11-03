import { Artist } from "./artist.model";
import { Image } from "./image.model";

export interface Album{
    id: number;
    name: string;
    artists: Artist[];
    totalTracks: number;
    releaseDate: string;
    smallImage: Image;
}