import { Album } from "./album.model";

export interface Track {
    name: string;
    number: number;
    explicit: boolean;
    album: Album;
}

export interface TrackPage {
  content: Track[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}