import ILocation from "./ILocation";

export type Rating = "Want to Go" | "Meh" | "Ok" | "Good" | "Better" | "Best";

export default interface IRestaurant {
    name: string;
    genre: string;
    subGenre: string;
    description?: string;
    rating: Rating;
    location: ILocation;
    reviewSites: { name: string, address: string }[];
    hasOtherLocations: boolean;
}
