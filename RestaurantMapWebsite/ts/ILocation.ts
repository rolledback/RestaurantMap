export default interface ILocation {
    address: string;
    lat: number;
    lng: number;
    reviewSites: { name: string, address: string }[];
    visited: boolean;
    marker?: google.maps.Marker;
}