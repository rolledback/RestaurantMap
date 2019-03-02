using LiteDB;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Auth;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using System.IO;
using Microsoft.WindowsAzure.Storage.Blob;

namespace RestaurantMapAPI
{
    public class BackupDbService : IBackupDbService
    {

        private CloudStorageAccount _account;
        private CloudBlobContainer _backupsContainer;

        public BackupDbService(IOptions<StorageSettings> settings)
        {
            this._account = new CloudStorageAccount(new StorageCredentials(settings.Value.AccountName, settings.Value.AccountKey), true);
            CloudBlobClient cloudBlobClient = _account.CreateCloudBlobClient();
            _backupsContainer = cloudBlobClient.GetContainerReference("dbbackup");
        }

        public async Task Restore<T>(IBackupable<T> db)
        {
            string backupName = db.GetBackupName();
            CloudBlockBlob cloudBlockBlob = _backupsContainer.GetBlockBlobReference(backupName);
            var json = await cloudBlockBlob.DownloadTextAsync();
            IEnumerable<T> data = JsonConvert.DeserializeObject<IEnumerable<T>>(json);
            db.RestoreFromBackup(data);
        }

        public async Task Backup<T>(IBackupable<T> db)
        {
            var allDocs = await db.GetAllForBackup();
            string json = JsonConvert.SerializeObject(allDocs);
            string backupName = db.GetBackupName();

            CloudBlockBlob cloudBlockBlob = _backupsContainer.GetBlockBlobReference(backupName);
            await cloudBlockBlob.SnapshotAsync();
            await cloudBlockBlob.UploadTextAsync(json);
        }
    }
}
