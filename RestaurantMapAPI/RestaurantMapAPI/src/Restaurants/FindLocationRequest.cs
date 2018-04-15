using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class FindLocationRequest
    {
        public string restaurantName { get; set; }
        public int numResults { get; set; }
    }
}
