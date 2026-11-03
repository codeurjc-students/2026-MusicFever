import { Album } from "./album.model";

export interface Track {
    name: string;
    number: number;
    explicit: boolean;
    album: Album;
}