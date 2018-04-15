using Microsoft.IdentityModel.Tokens;
using System.Text;

// Shamelessly taken from: https://github.com/TahirNaushad/Fiver.Security.Bearer/

namespace RestaurantMapAPI
{
    public static class JwtSecurityKey
    {
        public static SymmetricSecurityKey Create(string secret)
        {
            return new SymmetricSecurityKey(Encoding.ASCII.GetBytes(secret));
        }
    }
}