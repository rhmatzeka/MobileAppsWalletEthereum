package id.rahmat.projekakhir.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE walletAddress = :walletAddress ORDER BY timestamp DESC")
    LiveData<List<TransactionEntity>> observeAll(String walletAddress);

    @Query("SELECT * FROM transactions WHERE walletAddress = :walletAddress AND direction = :direction ORDER BY timestamp DESC")
    LiveData<List<TransactionEntity>> observeByDirection(String walletAddress, String direction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TransactionEntity transactionEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TransactionEntity> transactionEntities);
}
