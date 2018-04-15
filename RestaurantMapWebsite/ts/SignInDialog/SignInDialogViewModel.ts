import * as ko from "knockout";
import * as $ from "jquery";

export default class SignInDialogViewModel {
    private _usernameFieldValue: KnockoutObservable<string>;
    private _usernameFieldPlaceholder: KnockoutObservable<string>;

    private _passwordFieldValue: KnockoutObservable<string>;
    private _passwordFieldPlaceholder: KnockoutObservable<string>;

    public result: KnockoutObservable<{ username: string, password: string } | null>;

    constructor() {
        this.result = ko.observable<{ username: string, password: string } | null>();

        this._usernameFieldValue = ko.observable<string>("");
        this._usernameFieldPlaceholder = ko.observable<string>("Enter your username");

        this._passwordFieldValue = ko.observable<string>("");
        this._passwordFieldPlaceholder = ko.observable<string>("Enter your password");
    }

    public cancel = () => {
        $("#signInDialog").modal("hide");
        setTimeout(() => {
            // wait 100ms for dismiss animation
            this._reset();
        }, 100);
        this.result(null);
    }

    public signIn = () => {
        $("#signInDialog").modal("hide");
        setTimeout(() => {
            // wait 100ms for dismiss animation
            this._reset();
        }, 100);
        this.result({ username: this._usernameFieldValue(), password: this._passwordFieldValue() });
    }

    private _reset(): void {
        this._usernameFieldValue("");
        this._passwordFieldValue("");
    }
}