import * as ko from "knockout";
import * as $ from "jquery";
import PanelViewModel from "./PanelViewModel";
import SearchPanelViewModel from "./SearchPanelViewModel";
import ResultsPanelViewModel, { RestaurantSearchResult } from "./ResultsPanelViewModel";
import FinalPanelViewModel from "./FinalPanelViewModel";
import IRestaurant from "../IRestaurant";

export default class AddRestaurantDialogViewModel {
    private _currentStep: KnockoutObservable<PanelViewModel>;
    private _searchStep: SearchPanelViewModel;
    private _resultsStep: ResultsPanelViewModel;
    private _finalStep: FinalPanelViewModel;

    public result: KnockoutObservable<IRestaurant | null>;

    constructor() {
        this.result = ko.observable<any>();

        this._searchStep = new SearchPanelViewModel(this);
        this._resultsStep = new ResultsPanelViewModel(this);
        this._finalStep = new FinalPanelViewModel(this);
        this._currentStep = ko.observable<PanelViewModel>(this._searchStep);
        this.openSearchPanel();
    }

    public openSearchPanel() {
        this._searchStep.onShow();
        this._currentStep(this._searchStep);
    }

    public openResultsPanel(args: { results: RestaurantSearchResult[] }) {
        this._resultsStep.onShow(args);
        this._currentStep(this._resultsStep);
    }

    public openFinalPanel(args: { restaurant: IRestaurant }) {
        this._finalStep.onShow(args);
        this._currentStep(this._finalStep);
    }

    public close = (result?: any) => {
        $("#addRestaurantDialog").modal("hide");
        setTimeout(() => {
            // wait 100ms for dismiss animation
            this.openSearchPanel();
        }, 100);
        this.result(result);
    }
}