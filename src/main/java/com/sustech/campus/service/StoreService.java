package com.sustech.campus.service;

import com.sustech.campus.entity.Goods;
import com.sustech.campus.entity.GoodsPhoto;
import com.sustech.campus.entity.Store;

import java.math.BigDecimal;
import java.util.List;

public interface StoreService {
    /**
     * @return a List containing all stores
     */
    List<Store> listAllStores();

    /**
     * @param storeName
     * @return false if failed to add such store, otherwise, true
     */
    boolean addStore(String storeName);

    /**
     * @param storeName
     * @return null if the store with the given name does not exist, otherwise, a Store instance
     */
    Store getStoreByName(String storeName);

    /**
     * @param storeName
     * @return whether the store with the given name exists
     */
    boolean storeExists(String storeName);

    /**
     * @param storeName
     * @return whether deletion succeed
     */
    boolean deleteStore(String storeName);

    /**
     * @param storeName
     * @param goodsName
     * @param price
     * @param quantity
     * @return null if failed to add such store, otherwise, the id of the goods
     */
    Long addGoods(String storeName, String goodsName, BigDecimal price, Integer quantity);

    /**
     * @param storeName
     * @return null if such store does not exist, otherwise goods in the store with the given name
     */
    List<Goods> listAllGoods(String storeName);

    /**
     * @param goodsId
     * @return null if such goods do not exist, otherwise, the goods with the given id
     */
    Goods getGoodsById(Long goodsId);

    /**
     * @param storeName
     * @param goodsName
     * @return null if such goods do not exist, otherwise, the goods with the given name and in the store
     * with the given name
     */
    Goods getGoodsByName(String storeName, String goodsName);

    /**
     * @param goodsId
     * @return whether such goods exist
     */
    boolean goodsExists(Long goodsId);

    /**
     * @param goodsId
     * @return whether deletion succeed
     */
    boolean deleteGoods(Long goodsId);

    /**
     * @param goodsId
     * @param newPrice
     * @return false if failed to change the price of such goods, otherwise, true
     */
    boolean changeGoodsPrice(Long goodsId, BigDecimal newPrice);

    /**
     * @param goodsId
     * @param newQuantity
     * @return false if failed to change the quantity of such goods, otherwise, true
     */
    boolean changeGoodsQuantity(Long goodsId, Integer newQuantity);

    /**
     * @param goodsId
     * @param photoPath
     * @return null if failed to add such photo, otherwise, the id of the photo
     */
    Long addGoodsPhoto(Long goodsId, String photoPath);

    /**
     * @param photoId
     * @return null if photo with the given id does not exist, otherwise, a GoodsPhoto instance
     */
    GoodsPhoto getGoodsPhotoById(Long photoId);

    /**
     * @param photoId
     * @return whether the photo with the given id exists
     */
    boolean goodsPhotoExists(Long photoId);

    /**
     * @param goodsId
     * @return null if goods with the given id do not exist, otherwise, all photos of such goods
     */
    List<GoodsPhoto> listAllGoodsPhotos(Long goodsId);

    /**
     * @param photoId
     * @return whether deletion succeed
     */
    boolean deleteGoodsPhoto(Long photoId);
}