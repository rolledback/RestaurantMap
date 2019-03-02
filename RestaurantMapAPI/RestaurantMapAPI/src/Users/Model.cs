using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class User
    {
        public int id { get; set; }
        public string username { get; set; }
        public string hashedPassword { get; set; }
        public bool isAdmin { get; set; }
    }
}
