import * as ko from "knockout";
import IPanelViewModel from "./PanelViewModel";
import AddRestaurantDialogViewModel from "./AddRestaurantDialogViewModel";
import IRestaurant from "../IRestaurant";

export type RestaurantSearchResult = { restaurant: IRestaurant, selected: KnockoutObservable<boolean> };

export default class ResultsPanelViewModel extends IPanelViewModel {

    private _results: KnockoutObservableArray<RestaurantSearchResult>;
    public selectedResults: KnockoutObservableArray<RestaurantSearchResult>;

    constructor(dialogViewModel: AddRestaurantDialogViewModel) {
        super(dialogViewModel, "Next", "results");
        this._results = ko.observableArray<RestaurantSearchResult>();
        this.selectedResults = ko.observableArray<RestaurantSearchResult>();
    }

    public onShow(args: { results: RestaurantSearchResult[] }) {
        this._results(args.results);
        this.selectedResults.removeAll();
    }

    public onNext = () => {
        let restaurant: IRestaurant | undefined = undefined;
        this._results().forEach((result) => {
            if (result.selected()) {
                if (!restaurant) {
                    restaurant = result.restaurant;
                } else {
                    restaurant.locations.push(result.restaurant.locations[0]);
                }
            }
        });

        if (!!restaurant) {
            this.dialogViewModel.openFinalPanel({ restaurant: restaurant });
        }
    }
}