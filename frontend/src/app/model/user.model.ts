import { Image } from "./image.model";

export interface ActiveUser {
  username: string;
  profilePicture: Image;
}

export interface UserDisplay {
  username: string;
  profilePicture: Image;
  email: string;
  accountCreatedDate: string;
  musicProvider: string;
}