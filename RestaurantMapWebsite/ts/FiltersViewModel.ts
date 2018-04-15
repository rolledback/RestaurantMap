import * as ko from "knockout";
import FilterViewModel from "./FilterViewModel";

export default class FiltersViewModel {
    private _filters: KnockoutObservableArray<FilterViewModel>;

    public allSelectedFilters: KnockoutComputed<{ [field: string]: string[] }>;

    constructor() {
        this._filters = ko.observableArray();
        this.allSelectedFilters = ko.computed<{ [field: string]: string[] }>(() => {
            let retValue: { [field: string]: string[] } = {};
            this._filters().forEach((filterViewModel) => {
                retValue[filterViewModel.name] = [];
                filterViewModel.selectedFilters().forEach((filter) => retValue[filterViewModel.name].push(filter.name));
            });
            return retValue;
        });
    }

    public clearFilters(): void {
        this._filters.removeAll();
    }

    public addFilter(filter: FilterViewModel) {
        this._filters.push(filter);
    }
}
