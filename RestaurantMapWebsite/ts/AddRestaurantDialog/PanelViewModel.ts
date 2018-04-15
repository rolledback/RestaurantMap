import * as ko from "knockout";
import AddRestaurantDialogViewModel from "./AddRestaurantDialogViewModel";

export default abstract class PanelViewModel {
    public buttonText: string;
    public id: string;
    public dialogViewModel: AddRestaurantDialogViewModel;

    constructor(dialogViewModel: AddRestaurantDialogViewModel, buttonText: string, id: string) {
        this.buttonText = buttonText;
        this.id = id;
        this.dialogViewModel = dialogViewModel;
    }

    abstract onShow(args?: {});

    public onNext;
}