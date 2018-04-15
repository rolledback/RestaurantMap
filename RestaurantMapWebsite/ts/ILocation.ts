export default interface ILocation {
    address: string;
    reviewSites: { name: string, address: string }[];
    marker?: google.maps.Marker;
}