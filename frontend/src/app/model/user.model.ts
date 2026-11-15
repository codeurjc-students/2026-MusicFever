import { Image } from "./image.model";

export interface ActiveUser {
  username: string;
  image: Image;
}

export interface UserDisplay {
  username: string;
  image: Image;
  email: string;
  accountCreatedDate: string;
  musicProvider: string;
}