package com.example.soluemergencias.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.soluemergencias.data.data_objects.dbo.RutVinculadoDBO
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO

@Dao
interface RutVinculadoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardarRutVinculado(rutVinculadoDBO: RutVinculadoDBO)

    @Query("select * from RutVinculadoDBO")
    fun obtenerRutVinculados(): List<RutVinculadoDBO>

    @Query("delete from RutVinculadoDBO")
    fun eliminarRutVinculados()

}