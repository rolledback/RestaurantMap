using LiteDB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI.LiteDB
{
    public class RestaurantRepository : IRestaurantRepository
    {
        private readonly RestaurantContext _context;

        public RestaurantRepository()
        {
            _context = new RestaurantContext();
        }

        public async Task<IEnumerable<Restaurant>> GetAllRestaurants()
        {
            try
            {
                return _context.Restaurants
                        .Find(_ => true).ToList();
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

        public async Task<Restaurant> GetRestaurant(string id)
        {
            try
            {
                var filter = Query.EQ("Id", id);

                return _context.Restaurants
                    .Find(filter)
                    .FirstOrDefault();
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

        public async Task AddRestaurant(Restaurant item)
        {
            try
            {
                _context.Restaurants.Insert(item);
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

        public async Task<bool> RemoveRestaurant(string id)
        {
            try
            {
                int deletedDocs = _context.Restaurants.Delete(Query.EQ("Id", id));

                return deletedDocs > 0;
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }
    }
}
