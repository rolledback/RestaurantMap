import * as ko from "knockout";
import * as $ from "jquery";
import IRestaurant, { Rating } from "./IRestaurant";
import MapIcons from "./MapIcons";
import ILocation from "./ILocation";
import FiltersViewModel from "./FiltersViewModel";
import FilterViewModel from "./FilterViewModel";
import AddRestaurantDialogViewModel from "./AddRestaurantDialog/AddRestaurantDialogViewModel";
import SignInDialogViewModel from "./SignInDialog/SignInDialogViewModel";
import ApiClient from "./ApiClient";

export type AuthToken = { token: string, username: string };

export default class RestaurantMapViewModel {
    private _map: google.maps.Map;
    private _markers: google.maps.Marker[];
    private _geocoder: google.maps.Geocoder;
    private _currentOpenWindow: google.maps.InfoWindow;
    private _data: IRestaurant[] = [];
    private _filters: FiltersViewModel;

    private _addDialog: AddRestaurantDialogViewModel;
    private _signInDialog: SignInDialogViewModel;

    private _currentAccessToken: KnockoutObservable<AuthToken | null>;

    constructor() {
        this._geocoder = new google.maps.Geocoder();
        this._currentAccessToken = ko.observable<AuthToken | null>(null);

        let mapElement = document.getElementById("map");
        let mapOptions: google.maps.MapOptions = {
            center: {
                lat: 47.6144103,
                lng: -122.3046512
            },
            zoom: 12
        };

        this._map = new google.maps.Map(mapElement, mapOptions);
        this._filters = new FiltersViewModel();
        this._addLocationMarker();

        this._init();
        this._addDialog = new AddRestaurantDialogViewModel();
        this._addDialog.result.subscribe(async (result: IRestaurant | null) => {
            if (!!result) {
                try {
                    await this._addRestaurant(result);
                    await this._init();
                } catch (err) {
                    alert(err);
                }
            }
        });

        this._signInDialog = new SignInDialogViewModel();
        this._signInDialog.result.subscribe(async (result: { username: string, password: string } | null) => {
            if (!!result) {
                try {
                    this._currentAccessToken(await this.signIn(result));
                } catch (err) {
                    alert(err);
                }
            }
        });

        this._currentAccessToken.subscribe((value: AuthToken | null) => {
            ApiClient.setAccessToken(!!value ? value.token : null);
        });
    }

    public async signOut(): Promise<void> {
        this._currentAccessToken(null);
    }

    public async signIn(loginParams: { username: string, password: string }): Promise<AuthToken> {
        return await ApiClient.post<{ username: string, password: string }, AuthToken>("login", loginParams);
    }

    public async exportData(): Promise<void> {
        let restaurntsToExport: IRestaurant[] = [];
        this._data.forEach((restaurnt) => {
            let filterOut = !RestaurantMapViewModel._doesRestaurantMatchFilters(restaurnt, this._filters.allSelectedFilters());
            if (!filterOut) {
                restaurntsToExport.push(restaurnt);
            }
        });

        restaurntsToExport.sort((one, two) => {
            if (one.genre !== two.genre) {
                return one.genre > two.genre ? 1 : -1;
            } else if (one.rating !== two.rating) {
                if (one.rating === "Best") {
                    return -1;
                } else if (two.rating === "Best") {
                    return 1;
                } else if (one.rating === "Better") {
                    return -1;
                } else if (two.rating === "Better") {
                    return 1;
                } else if (one.rating === "Good") {
                    return -1;
                } else if (two.rating === "Good") {
                    return 1;
                } else if (one.rating === "Ok") {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return one.subGenre > two.subGenre ? 1 : -1;
            }
        });

        let fileContents = "Name,Genre,Sub-Genre,Rating\n";
        restaurntsToExport.forEach((restaurant) => {
            fileContents += `${restaurant.name},${restaurant.genre},${restaurant.subGenre},${restaurant.rating}\n`;
        });

        let element = document.createElement("a");
        element.setAttribute("href", "data:text/plain;charset=utf-8," + encodeURIComponent(fileContents));
        element.setAttribute("download", "SeattleFood.csv");

        element.style.display = "none";
        document.body.appendChild(element);

        element.click();

        document.body.removeChild(element);
    }

    private async _init(): Promise<void> {
        await this._getData();
        this._addMarkers();
        this._generateFilters();
    }

    private async _addRestaurant(restaurant: IRestaurant): Promise<void> {
        return await ApiClient.post<IRestaurant, void>("restaurants", restaurant);
    }

    private _generateFilters() {
        this._filters.clearFilters();
        this._filters.addFilter(this._createFilter("genre"));
        this._filters.addFilter(this._createFilter("rating"));

        this._filters.allSelectedFilters.subscribe((allSelectedFilters) => {
            this._data.forEach((restaurnt) => {
                let filterOut = !RestaurantMapViewModel._doesRestaurantMatchFilters(restaurnt, allSelectedFilters);
                this._filterRestaurant(restaurnt, filterOut);
            });
        });
    }

    private _createFilter(field: string): FilterViewModel {
        let fieldValues = {};
        this._data.forEach((restaurant: IRestaurant) => fieldValues[restaurant[field]] = null);
        let fieldFilter = new FilterViewModel(RestaurantMapViewModel._toTitleCase(field), Object.keys(fieldValues));
        return fieldFilter;
    }

    private _filterRestaurant(restaurant: IRestaurant, filteredOut: boolean) {
        restaurant.locations.forEach((location) => {
            if (!!location.marker) {
                if (!filteredOut && location.marker.getMap() === null) {
                    location.marker.setMap(this._map);
                } else if (filteredOut && !!location.marker.getMap()) {
                    location.marker.setMap(null);
                }
            }
        });
    }

    private async _getData(): Promise<void> {
        try {
            this._data = await ApiClient.get<IRestaurant[]>("restaurants");
        } catch (err) {
            this._data = [];
            alert(err);
        }
    }

    private _addMarkers() {
        if (!this._markers) {
            this._markers = [];
        } else {
            this._markers.forEach((marker) => {
                marker.setMap(null);
            });
            this._markers = [];
        }

        this._data.forEach((restaurant) => {
            this._addRestaurantMarkers(restaurant);
        });

        this._map.addListener("click", () => {
            if (!!this._currentOpenWindow) {
                this._currentOpenWindow.close();
            }
        });
    }

    private _addRestaurantMarkers(restaurant: IRestaurant) {
        restaurant.locations.forEach((location) => {
            this._geocoder.geocode({ "address": location.address }, (results, status) => {
                if (status == google.maps.GeocoderStatus.OK) {
                    let infoWindow = this._createInfoWindow(restaurant, location);
                    let marker = new google.maps.Marker({
                        position: results[0].geometry.location,
                        map: this._map,
                        icon: RestaurantMapViewModel._ratingToIcon(restaurant.rating)
                    });
                    marker.addListener("click", () => {
                        if (!!this._currentOpenWindow) {
                            this._currentOpenWindow.close();
                        }
                        this._currentOpenWindow = infoWindow;
                        infoWindow.open(this._map, marker);
                    });
                    location.marker = marker;
                } else {
                    // fail silently
                }
            });
        });
    }

    private _createInfoWindow(restaurant: IRestaurant, location: ILocation): google.maps.InfoWindow {
        let contentString: string = `<h3>${restaurant.name} - ${restaurant.rating}</h3>\n` +
            `<p>${restaurant.genre} | ${restaurant.subGenre}, ${location.address}</p>\n`;
        contentString += `<a href="https://www.google.com/maps/search/?api=1&query=${encodeURI(restaurant.name + " " + location.address)}" target="_blank">Open In Google Maps</a>\n`;
        contentString += "<br><br>Review sites:<ul>\n";
        location.reviewSites.forEach((site) => {
            contentString += `<li><a href=${site} target="_blank">${site}</a></li>\n`;
        });
        contentString += "<ul>\n";
        return new google.maps.InfoWindow({ content: contentString });
    }

    private _addLocationMarker() {
        // ToDo: use this library: https://chadkillingsworth.github.io/geolocation-marker/
        if (navigator.geolocation) {
            // ToDo: use navigator.geolocation.watchPosition
            navigator.geolocation.getCurrentPosition((position) => {
                // outerMarker
                new google.maps.Marker({
                    clickable: false,
                    cursor: 'pointer',
                    title: 'Current location',
                    zIndex: 3,
                    position: {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude
                    },
                    map: this._map,
                    icon: {
                        path: google.maps.SymbolPath.CIRCLE,
                        fillColor: "#C8D6EC",
                        fillOpacity: 0.7,
                        scale: 12,
                        strokeWeight: 0,
                    }
                });
                // innerMarker
                new google.maps.Marker({
                    clickable: false,
                    cursor: 'pointer',
                    title: 'Current location',
                    zIndex: 3,
                    position: {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude
                    },
                    map: this._map,
                    icon: {
                        path: google.maps.SymbolPath.CIRCLE,
                        fillColor: '#4285F4',
                        fillOpacity: 1,
                        scale: 6,
                        strokeColor: 'white',
                        strokeWeight: 2,
                    }
                });
            });
        }
    }

    private static _ratingToIcon(rating: Rating): string {
        switch (rating) {
            case "Ok":
                return MapIcons.orange_dot;
            case "Good":
                return MapIcons.blue_dot;
            case "Better":
                return MapIcons.yellow_dot;
            case "Best":
                return MapIcons.green_dot;
        }
    }

    private static _toTitleCase(str) {
        return str.replace(/\w\S*/g, function (txt) { return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase(); });
    }

    private static _doesRestaurantMatchFilters(restaurant: IRestaurant, filters: { [field: string]: string[] }): boolean {
        for (let filter in filters) {
            let field = filter.toLowerCase();
            if (!filters[filter].some((value) => restaurant[field] === value)) {
                return false;
            }
        }
        return true;
    }

}
