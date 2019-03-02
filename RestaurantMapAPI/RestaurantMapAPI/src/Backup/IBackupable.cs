using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public interface IBackupable<T>
    {
        Task<IEnumerable<T>> GetAllForBackup();

        void RestoreFromBackup(IEnumerable<T> backup);

        string GetBackupName();
    }
}
