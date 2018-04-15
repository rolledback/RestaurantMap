import RestaurantMapViewModel from "./RestaurantMapViewModel";
import * as $ from "jquery";
import "popper.js";
import "bootstrap";
import * as ko from "knockout";

var restaurantMapViewModel: RestaurantMapViewModel;
function initMap() {
    restaurantMapViewModel = new RestaurantMapViewModel();
    ko.applyBindings(restaurantMapViewModel);
}

// disable the bootstrap dropdown-item click
$(".dropdown-item .filter-value-button").click(() => { return true; });
initMap();
