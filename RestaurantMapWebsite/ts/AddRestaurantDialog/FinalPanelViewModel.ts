import * as ko from "knockout";
import IPanelViewModel from "./PanelViewModel";
import AddRestaurantDialogViewModel from "./AddRestaurantDialogViewModel";
import IRestaurant, { Rating } from "../IRestaurant";

export default class FinalPanelViewModel extends IPanelViewModel {

    private _genreFieldValue: KnockoutObservable<string>;
    private _genreFieldPlaceholder: string;
    private _subGenreFieldValue: KnockoutObservable<string>;
    private _subGenreFieldPlaceholder: string;
    private _rating: KnockoutObservable<Rating>;
    private _restaurant: IRestaurant;

    constructor(dialogViewModel: AddRestaurantDialogViewModel) {
        super(dialogViewModel, "Submit", "final");
        this._genreFieldValue = ko.observable<string>("");
        this._genreFieldPlaceholder = "Enter a genre";
        this._subGenreFieldValue = ko.observable<string>("");
        this._subGenreFieldPlaceholder = "Enter a sub-genre";
        this._rating = ko.observable<Rating>("Ok");
    }

    public onShow(args: { restaurant: IRestaurant }) {
        this._restaurant = args.restaurant;
        this._genreFieldValue("");
        this._subGenreFieldValue("");
        this._rating("Ok");
    }

    public onNext = () => {
        if (!!this._genreFieldValue()) {
            this._restaurant.genre = this._genreFieldValue();
        } else {
            return;
        }

        if (!!this._subGenreFieldValue()) {
            this._restaurant.subGenre = this._subGenreFieldValue();
        } else {
            return;
        }

        if (!!this._rating()) {
            this._restaurant.rating = this._rating();
        } else {
            return;
        }

        this.dialogViewModel.close(this._restaurant);
    }
}