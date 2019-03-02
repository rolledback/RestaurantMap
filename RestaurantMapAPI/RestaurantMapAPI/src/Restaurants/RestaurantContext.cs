using LiteDB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI.LiteDB
{
    public class RestaurantContext
    {
        private readonly LiteDatabase _database;

        public RestaurantContext()
        {
            _database = new LiteDatabase("./RestaurantMap.db");
        }

        public LiteCollection<Restaurant> Restaurants
        {
            get
            {
                return _database.GetCollection<Restaurant>("Restaurants");
            }
        }
    }
}
