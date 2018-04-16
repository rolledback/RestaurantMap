using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Extensions.Options;
using MongoDB.Bson;
using MongoDB.Driver;

namespace RestaurantMapAPI
{
    public class RestaurantRepository : IRestaurantRepository
    {
        private readonly RestaurantsContext _context = null;

        public RestaurantRepository(IOptions<MongoDbSettings> settings)
        {
            _context = new RestaurantsContext(settings);
        }

        public async Task<IEnumerable<Restaurant>> GetAllRestaurants()
        {
            try
            {
                return await _context.Restaurants
                        .Find(_ => true).ToListAsync();
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

        public async Task<Restaurant> GetRestaurant(string id)
        {
            var filter = Builders<Restaurant>.Filter.Eq(r => r._id.ToString(), id);

            try
            {
                return await _context.Restaurants
                                .Find(filter)
                                .FirstOrDefaultAsync();
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
                await _context.Restaurants.InsertOneAsync(item);
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
                DeleteResult actionResult = await _context.Restaurants.DeleteOneAsync(Builders<Restaurant>.Filter.Eq("Id", id));

                return actionResult.IsAcknowledged && actionResult.DeletedCount > 0;
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

        public async Task<bool> UpdateRestaurant(Restaurant item)
        {
            try
            {
                var filter = Builders<Restaurant>.Filter.Eq("_id", item._id);
                ReplaceOneResult actionResult = await _context.Restaurants.ReplaceOneAsync(filter, item, new UpdateOptions { IsUpsert = true });
                return actionResult.IsAcknowledged && actionResult.ModifiedCount > 0;
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

        public async Task<bool> RemoveAllRestaurants()
        {
            try
            {
                DeleteResult actionResult = await _context.Restaurants.DeleteManyAsync(new BsonDocument());

                return actionResult.IsAcknowledged && actionResult.DeletedCount > 0;
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

    }
}
