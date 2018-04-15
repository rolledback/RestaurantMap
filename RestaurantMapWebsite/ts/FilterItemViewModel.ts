import * as ko from "knockout";
import FilterViewModel from "./FilterViewModel";

export default class FilterItemViewModel {
    public name: string;
    public selected: KnockoutComputed<boolean>;

    constructor(name: string, parent: FilterViewModel) {
        this.name = name;
        this.selected = ko.computed({
            read: () => {
                return parent.selectedFilters.indexOf(this) > -1;
            },
            write: (newValue) => {
                if (newValue) {
                    parent.selectedFilters.push(this);
                } else {
                    parent.selectedFilters.remove(this);
                }
            }
        });
        this.selected(true);
    }

    private _clickHandler() {
        this.selected(!this.selected());
    }
}
