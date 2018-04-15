import ILocation from "./ILocation";

export type Rating = "Ok" | "Good" | "Better" | "Best";

export default interface IRestaurant {
    name: string;
    genre: string;
    subGenre: string;
    description?: string;
    rating: Rating;
    locations: ILocation[];
}
