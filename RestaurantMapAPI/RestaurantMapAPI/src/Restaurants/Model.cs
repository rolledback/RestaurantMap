﻿using LiteDB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class Restaurant
    {
        public int id { get; set; }
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
        public double lat { get; set; }
        public double lng { get; set; }
        public IEnumerable<string> reviewSites { get; set; }
        public bool visited { get; set; }
    }

    public class ReviewSite
    {
        public string address { get; set; }
        public string name { get; set; }
    }
}
