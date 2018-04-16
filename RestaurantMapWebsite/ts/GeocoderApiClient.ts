import ILocation from "./ILocation";

class GeocoderApiClient {

    private _geocoder: google.maps.Geocoder;
    private static _maxAttempts: number = 25;

    constructor() {
        this._geocoder = new google.maps.Geocoder();
    }

    public async geocodeAddress(address: string, attempts: number = GeocoderApiClient._maxAttempts): Promise<google.maps.LatLng> {
        return new Promise<google.maps.LatLng>((resolve, reject) => {
            this._geocoder.geocode({ "address": address }, (results, status) => {
                if (status === google.maps.GeocoderStatus.OK) {
                    resolve(results[0].geometry.location);
                } else if (status === google.maps.GeocoderStatus.OVER_QUERY_LIMIT && attempts > 0) {
                    setTimeout(async () => {
                        resolve(await this.geocodeAddress(address, attempts - 1));
                    }, 100);
                } else {
                    reject(status);
                }
            });
        });
    }
}

export default new GeocoderApiClient();