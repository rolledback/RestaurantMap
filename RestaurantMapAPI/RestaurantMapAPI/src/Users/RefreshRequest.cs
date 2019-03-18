using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class RefreshRequest
    {
        public string username { get; set; }
        public string accessToken { get; set; }
        public string refreshToken { get; set; }
    }
}
