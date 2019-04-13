using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using Microsoft.Extensions.Options;
using System.Security.Cryptography;

namespace RestaurantMapAPI
{
    [Route("api")]
    public class RestaurantAPIController
    {
        private readonly IRestaurantRepository _RestaurantRepository;
        private readonly IUserRepository _UsersRepository;
        private readonly IOptions<SecretSettings> _SecretSettings;
        private readonly IOptions<StorageSettings> _StorageSettings;

        public RestaurantAPIController(IRestaurantRepository RestaurantRepository, IUserRepository UsersRepository, IOptions<SecretSettings> secretSettings, IOptions<StorageSettings> storageSettings)
        {
            _RestaurantRepository = RestaurantRepository;
            _UsersRepository = UsersRepository;
            _SecretSettings = secretSettings;
            _StorageSettings = storageSettings;
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

        [Authorize]
        [HttpPost("db/restaurants")]
        public async Task UpdateRestaurantsDb([FromBody]UpdateDbRequest value)
        {
            BackupDbService backupService = new BackupDbService(_StorageSettings);
            await backupService.Restore<Restaurant>(value.blobName, _RestaurantRepository);
        }

        [Authorize]
        [HttpGet("users")]
        public async Task<IEnumerable<DbUser>> GetUsers()
        {
            return await _UsersRepository.GetAllUsers();
        }

        [HttpPost("auth/login")]
        public async Task<dynamic> Login([FromBody]LoginRequest value)
        {
            var hashedPassword = SeasonAndHashPassword(value.password);
            var users = await this._UsersRepository.GetAllUsers();
            var matchingUser = users.Where(u => u.username == value.username && hashedPassword == u.hashedPassword).FirstOrDefault();

            if (matchingUser != null)
            {
                var authResult = GetAuthResult(matchingUser);
                await UpdateUserSessions(matchingUser, authResult, null);

                return authResult;
            }
            else
            {
                return new StatusCodeResult(401);
            }
        }

        [HttpGet("auth/refresh")]
        public async Task<dynamic> Refresh([FromBody]RefreshRequest value)
        {
            var users = await this._UsersRepository.GetAllUsers();
            var matchingUser = users.Where(u => u.username == value.username).FirstOrDefault();

            if (matchingUser != null)
            {
                var matchingSession = matchingUser.sessions.Where(s => s.accessToken == value.accessToken && s.refreshToken == value.refreshToken).FirstOrDefault();
                if (matchingSession != null)
                {
                    var currentTime = (long)(DateTime.UtcNow.Subtract(new DateTime(1970, 1, 1))).TotalMilliseconds;
                    if (matchingSession.expiration < currentTime)
                    {
                        return new StatusCodeResult(401);
                    }

                    var authResult = GetAuthResult(matchingUser);
                    await UpdateUserSessions(matchingUser, authResult, value);

                    return authResult;
                }
                else
                {
                    return new StatusCodeResult(401);
                }
            }
            else
            {
                return new StatusCodeResult(401);
            }
        }

        private async Task UpdateUserSessions(DbUser user, AuthResult authResult, RefreshRequest refreshRequest)
        {
            var expirationDate = DateTime.UtcNow.Add(new TimeSpan(24 * 30, 0, 0));
            var expirationEpoch = (long)(expirationDate.Subtract(new DateTime(1970, 1, 1))).TotalMilliseconds; 

            Session newSession = new Session();
            newSession.accessToken = authResult.accessToken;
            newSession.refreshToken = authResult.refreshToken;
            newSession.expiration = expirationEpoch;

            List<Session> newSessions = new List<Session>();
            if (refreshRequest != null)
            {
                newSessions = user.sessions.Where(s => !(s.accessToken == refreshRequest.accessToken && s.refreshToken == refreshRequest.refreshToken)).ToList();
            }
            else if (user.sessions != null)
            {
                newSessions = user.sessions.ToList();
            }
            newSessions.Add(newSession);
            user.sessions = newSessions;

            await _UsersRepository.UpdateUser(user);
        }

        private AuthResult GetAuthResult(DbUser dbUser)
        {
            var token = GenerateAccessToken(dbUser);
            var refreshToken = GenerateRefreshToken();

            var user = new User();
            user.username = dbUser.username;
            user.isAdmin = dbUser.isAdmin;
            user.id = dbUser.id;

            var authResult = new AuthResult();
            authResult.accessToken = token.Value;
            authResult.refreshToken = refreshToken;
            authResult.user = user;
            return authResult;
        }

        private string SeasonAndHashPassword(string password)
        {
            var salt = _SecretSettings.Value.Salt;
            var pepper = "12a736a1-d722-4869-afeb-7bbb113ca8d6";
            var seasonedPassword = salt + password + pepper;
            var hashedPassword = SHA.GenerateSHA512String(seasonedPassword);
            return hashedPassword;
        }

        private JwtToken GenerateAccessToken(DbUser user)
        {
           return new JwtTokenBuilder()
            .AddSecurityKey(JwtSecurityKey.Create(_SecretSettings.Value.TokenIssuerSigningKey))
            .AddSubject("authentication")
            .AddIssuer("restaurantmap.security.bearer")
            .AddAudience("restaurantmap.security.bearer")
            .AddExpiry(180)
            .Build();
        }

        private string GenerateRefreshToken()
        {
            var randomNumber = new byte[32];
            using (var rng = RandomNumberGenerator.Create())
            {
                rng.GetBytes(randomNumber);
                return Convert.ToBase64String(randomNumber);
            }
        }
    }
}
