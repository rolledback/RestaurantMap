using System.Threading;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public interface IScheduledTask
    {
        string Schedule { get; }
        Task ExecuteAsync(CancellationToken cancellationToken);
    }
}