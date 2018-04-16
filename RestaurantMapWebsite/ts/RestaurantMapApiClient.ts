class RestaurantMapApiClient {

    private static _baseEndpoint: string = "https://restaurantmapapi.azurewebsites.net/api/";
    // private static _baseEndpoint: string = "http://localhost:5000/api/";

    private _accessToken: string | null;

    constructor() {
        this._accessToken = null;
    }

    public setAccessToken(accessToken: string | null) {
        this._accessToken = accessToken;
    }

    public async get<ReturnT>(endpoint: string): Promise<ReturnT> {
        return await new Promise<ReturnT>((resolve, reject) => {
            let httpRequest = new XMLHttpRequest();
            httpRequest.onreadystatechange = () => {
                if (httpRequest.readyState === XMLHttpRequest.DONE) {
                    if (httpRequest.status === 401) {
                        reject("Unable to authenticate");
                    } else if (httpRequest.status !== 200) {
                        reject("Server not available");
                    } else {
                        resolve(JSON.parse(httpRequest.response));
                    }
                }
            };
            httpRequest.open("GET", `${RestaurantMapApiClient._baseEndpoint}${endpoint}`);
            httpRequest.setRequestHeader("Content-Type", "application/json");
            if (!!this._accessToken) {
                httpRequest.setRequestHeader("Authorization", `Bearer ${this._accessToken}`);
            }
            httpRequest.send();
        });
    }

    public async post<PayloadT, ReturnT>(endpoint: string, payload: PayloadT): Promise<ReturnT> {
        return await new Promise<ReturnT>((resolve, reject) => {
            let httpRequest = new XMLHttpRequest();
            httpRequest.onreadystatechange = () => {
                if (httpRequest.readyState === XMLHttpRequest.DONE) {
                    if (httpRequest.status === 401) {
                        reject("Unable to authenticate");
                    } else if (httpRequest.status !== 200) {
                        reject("Server unavailable");
                    } else {
                        if (!!httpRequest.response) {
                            resolve(JSON.parse(httpRequest.response));
                        } else {
                            resolve();
                        }
                    }
                }
            };
            httpRequest.open("POST", `${RestaurantMapApiClient._baseEndpoint}${endpoint}`);
            httpRequest.setRequestHeader("Content-Type", "application/json");
            if (!!this._accessToken) {
                httpRequest.setRequestHeader("Authorization", `Bearer ${this._accessToken}`);
            }
            httpRequest.send(JSON.stringify(payload));
        });
    }
}

export default new RestaurantMapApiClient();
