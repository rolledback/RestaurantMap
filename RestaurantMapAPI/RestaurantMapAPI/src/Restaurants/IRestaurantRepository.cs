using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

// Shamelessly taken from: http://www.qappdesign.com/using-mongodb-with-net-core-webapi/

namespace RestaurantMapAPI
{
    public interface IRestaurantRepository
    {
        Task<IEnumerable<Restaurant>> GetAllRestaurants();
        Task<Restaurant> GetRestaurant(string id);

        Task AddRestaurant(Restaurant item);

        Task<bool> UpdateRestaurant(string id, Restaurant item);

        Task<bool> RemoveRestaurant(string id);
    }
}
