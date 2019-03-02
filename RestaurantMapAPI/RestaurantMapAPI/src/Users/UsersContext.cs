using LiteDB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class UsersContext
    {
        private readonly LiteDatabase _database;

        public UsersContext()
        {
            _database = new LiteDatabase("./RestaurantMap.db");
        }

        public LiteCollection<User> Users
        {
            get
            {
                return _database.GetCollection<User>("Users");
            }
        }
    }
}
