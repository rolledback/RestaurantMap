using MongoDB.Bson;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class Restaurant
    {
        public ObjectId _id { get; set; }
        public string name { get; set; }
        public string genre { get; set; }

        public string subGenre { get; set; }
        public string description { get; set; }
        public string rating { get; set; }
        public IEnumerable<Location> locations { get; set; }
    }

    public class Location
    {
        public string address { get; set; }
        public IEnumerable<string> reviewSites { get; set; }
    }

    public class ReviewSite
    {
        public string address { get; set; }
        public string name { get; set; }
    }
}
