export default interface ILocation {
    address: string;
    lat: number;
    lng: number;
    reviewSites: { name: string, address: string }[];
    marker?: google.maps.Marker;
}