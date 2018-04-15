using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;

namespace RestaurantMapAPI
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddMvc();
            services.Configure<MongoDbSettings>(options =>
            {
                options.ConnectionString = Configuration.GetSection("MongoConnection:ConnectionString").Value;
                options.Database = Configuration.GetSection("MongoConnection:Database").Value;
            });

            var tokenIssuerSigningKey = Configuration.GetSection("SecretSettings:TokenIssuerSigningKey").Value;
            services.Configure<SecretSettings>(options =>
            {
                options.YelpApiKey = Configuration.GetSection("SecretSettings:YelpApiKey").Value;
                options.TokenIssuerSigningKey = tokenIssuerSigningKey;
                options.Salt = Configuration.GetSection("SecretSettings:Salt").Value; ;
            });

            services.AddTransient<IRestaurantRepository, RestaurantRepository>();
            services.AddTransient<IUsersRepository, UsersRepository>();
            services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
              .AddJwtBearer(options => {
                  options.TokenValidationParameters =
                       new TokenValidationParameters
                       {
                           ValidateIssuer = true,
                           ValidateAudience = true,
                           ValidateLifetime = true,
                           ValidateIssuerSigningKey = true,

                           ValidIssuer = "restaurantmap.security.bearer",
                           ValidAudience = "restaurantmap.security.bearer",
                           IssuerSigningKey = JwtSecurityKey.Create(tokenIssuerSigningKey)
                       };
              });

        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            app.UseCors(builder => builder.WithOrigins("*").WithHeaders("*").WithMethods("*"));
            app.UseAuthentication();
            app.UseMvc();
        }
    }
}
