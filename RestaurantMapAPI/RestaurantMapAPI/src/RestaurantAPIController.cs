using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using Microsoft.Extensions.Options;

namespace RestaurantMapAPI
{
    [Route("api")]
    public class RestaurantAPIController
    {
        private readonly IRestaurantRepository _RestaurantRepository;
        private readonly IUserRepository _UsersRepository;
        private readonly IOptions<SecretSettings> _SecretSettings;

        public RestaurantAPIController(IRestaurantRepository RestaurantRepository, IUserRepository UsersRepository, IOptions<SecretSettings> settings)
        {
            _RestaurantRepository = RestaurantRepository;
            _UsersRepository = UsersRepository;
            _SecretSettings = settings;
        }

        [Authorize]
        [HttpGet("find-restaurant")]
        public async Task<IEnumerable<Restaurant>> FindLocation(FindLocationRequest request)
        {
            var client = new Yelp.Api.Client(_SecretSettings.Value.YelpApiKey);
            var searchRequest = new Yelp.Api.Models.SearchRequest();
            searchRequest.Term = request.restaurantName;
            searchRequest.Location = "Seattle, WA";
            var results = await client.SearchBusinessesAllAsync(searchRequest);

            var matchesToAdd = request.numResults != 0 ? request.numResults : 10;
            var possibleLocations = new List<Restaurant>();
            foreach (var business in results.Businesses.Where(b => b.Location.City.Equals("Seattle")))
            {
                if (matchesToAdd == 0)
                {
                    break;
                }
                possibleLocations.Add(YelpBusinessToRestaurant(business));
                matchesToAdd--;
            }
            return possibleLocations;
        }

        private static Restaurant YelpBusinessToRestaurant(Yelp.Api.Models.BusinessResponse business)
        {
            var location = new Location();
            location.address = business.Location.Address1;

            if (!string.IsNullOrEmpty(business.Location.Address2))
            {
                location.address += " " + business.Location.Address2;
            }
            if (!string.IsNullOrEmpty(business.Location.Address3))
            {
                location.address += " " + business.Location.Address3;
            }

            location.address += " " + business.Location.City + " " + business.Location.State;
            location.reviewSites = new List<string> { business.Url.Split('?')[0] };

            var restaurant = new Restaurant();
            restaurant.name = business.Name;
            restaurant.genre = "";
            restaurant.subGenre = "";
            restaurant.description = "";
            restaurant.rating = "";
            restaurant.locations = new List<Location>{ location };

            return restaurant;
        }

        [HttpGet("restaurants")]
        public async Task<IEnumerable<Restaurant>> GetRestaurants()
        {
            return await _RestaurantRepository.GetAllRestaurants();
        }

        [Authorize]
        [HttpPost("restaurants")]
        public void AddRestaurant([FromBody]Restaurant value)
        {
           _RestaurantRepository.AddRestaurant(value);
        }

        [HttpPost("login")]
        public async Task<dynamic> Login([FromBody]LoginRequest value)
        {
            var hashedPassword = SeasonAndHashPassword(value.password);
            var users = await this._UsersRepository.GetAllUsers();
            var matchingUser = users.Where(u => u.username == value.username && hashedPassword == u.hashedPassword).FirstOrDefault();

            if (matchingUser != null)
            {
                var token = new JwtTokenBuilder()
                             .AddSecurityKey(JwtSecurityKey.Create(_SecretSettings.Value.TokenIssuerSigningKey))
                             .AddSubject("authentication")
                             .AddIssuer("restaurantmap.security.bearer")
                             .AddAudience("restaurantmap.security.bearer")
                             .AddExpiry(180)
                             .Build();

                return new { token = token.Value, username = value.username };
            }
            else
            {
                return new StatusCodeResult(401);
            }
        }

        private string SeasonAndHashPassword(string password)
        {
            var salt = _SecretSettings.Value.Salt;
            var pepper = "12a736a1-d722-4869-afeb-7bbb113ca8d6";
            var seasonedPassword = salt + password + pepper;
            var hashedPassword = SHA.GenerateSHA512String(seasonedPassword);
            return hashedPassword;
        }
    }
}
