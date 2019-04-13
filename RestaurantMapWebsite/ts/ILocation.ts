export default interface ILocation {
    address: string;
    lat: number;
    lng: number;
    marker?: google.maps.Marker;
}