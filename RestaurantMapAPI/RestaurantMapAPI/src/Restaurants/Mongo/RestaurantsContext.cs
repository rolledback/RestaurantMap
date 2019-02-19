using Microsoft.Extensions.Options;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI.Mongo
{
    public class RestaurantsContext
    {
        private readonly IMongoDatabase _database = null;

        public RestaurantsContext(IOptions<MongoDbSettings> settings)
        {
            var client = new MongoClient(settings.Value.ConnectionString);
            if (client != null)
            {
                _database = client.GetDatabase(settings.Value.Database);
            }
        }

        public IMongoCollection<Restaurant> Restaurants
        {
            get
            {
                return _database.GetCollection<Restaurant>("Restaurants");
            }
        }
    }
}
