import * as ko from "knockout";
import FilterItemViewModel from "./FilterItemViewModel";

export default class FilterListViewModel {
    private _filters: KnockoutObservableArray<FilterItemViewModel>;
    private _allSelected: KnockoutComputed<boolean>;

    public selectedFilters: KnockoutObservableArray<FilterItemViewModel>;
    public name: string;

    constructor(name: string, filters: string[]) {
        this.name = name;
        this._filters = ko.observableArray();
        this.selectedFilters = ko.observableArray();

        filters.forEach((value: string) => this._filters.push(new FilterItemViewModel(value, this)));

        this._allSelected = ko.computed({
            read: () => {
                return this._filters().length === this.selectedFilters().length;
            },
            write: (newValue) => {
                if (newValue) {
                    this.selectedFilters(this._filters().slice(0));
                } else {
                    this.selectedFilters.removeAll();
                }
            }
        });
    }

    public _allClickHandler() {
        this._allSelected(!this._allSelected());
    }
}