import * as ko from "knockout";
import IPanelViewModel from "./PanelViewModel";
import { RestaurantSearchResult } from "./ResultsPanelViewModel";
import AddRestaurantDialogViewModel from "./AddRestaurantDialogViewModel";
import IRestaurant from "../IRestaurant";
import ApiClient from "../ApiClient";

export default class SearchPanelViewModel extends IPanelViewModel {

    private _searchFieldValue: KnockoutObservable<string>;
    private _searchFieldPlaceholder: string;

    constructor(dialogViewModel: AddRestaurantDialogViewModel) {
        super(dialogViewModel, "Search", "search");
        this._searchFieldValue = ko.observable<string>("");
        this._searchFieldPlaceholder = "Enter the name of a restaurant in Seattle";
    }

    public onShow() {
        this._searchFieldValue("");
    }

    public onNext = async () => {
        try {
            let restaurants: IRestaurant[] = await ApiClient.get<IRestaurant[]>(`find-restaurant?restaurantName=${this._searchFieldValue()}`);
            let results: RestaurantSearchResult[] = restaurants.map<RestaurantSearchResult>((searchResult) => {
                return {
                    restaurant: searchResult,
                    selected: ko.observable<boolean>(false)
                };
            });
            this.dialogViewModel.openResultsPanel({
                results: results
            });
        } catch (err) {
            alert("Server not available");
            this.dialogViewModel.close();
        }
    }
}