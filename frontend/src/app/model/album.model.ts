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

export interface AlbumPage {
  content: Album[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface AlbumDisplay {
  id: number;
  name: string;
  artists: Artist[];
  totalTracks: number;
  genre: string;
  bigImage: Image;
  releaseDate: string;
}