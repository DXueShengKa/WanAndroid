package com.hc.wanandroid.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.hc.wanandroid.db.entity.City
import com.hc.wanandroid.db.entity.County
import com.hc.wanandroid.db.entity.Province
import com.hc.wanandroid.db.entity.Town

@Dao
interface AddressDao {

    @Query("select * from province")
    suspend fun getAllProvince():Array<Province>

    @Query("select * from city where province_id = :provinceId ")
    suspend fun getCityByProvinceId(provinceId:String):Array<City>

    @Query("select * from county where city_id = :cityId ")
    suspend fun getCountyByCityId(cityId:String):Array<County>

    @Query("select * from town where county_id = :countyId ")
    suspend fun getTownByCountyId(countyId:String):Array<Town>

}