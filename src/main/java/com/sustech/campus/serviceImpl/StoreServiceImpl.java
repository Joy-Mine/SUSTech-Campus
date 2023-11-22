package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.Goods;
import com.sustech.campus.entity.GoodsPhoto;
import com.sustech.campus.entity.Store;
import com.sustech.campus.mapper.GoodsMapper;
import com.sustech.campus.mapper.GoodsPhotoMapper;
import com.sustech.campus.mapper.StoreMapper;
import com.sustech.campus.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    private StoreMapper storeMapper;

    private GoodsMapper goodsMapper;

    private GoodsPhotoMapper goodsPhotoMapper;

    @Autowired
    public StoreServiceImpl(StoreMapper storeMapper, GoodsMapper goodsMapper, GoodsPhotoMapper goodsPhotoMapper) {
        this.storeMapper = storeMapper;
        this.goodsMapper = goodsMapper;
        this.goodsPhotoMapper = goodsPhotoMapper;
    }

    @Override
    public List<Store> listAllStores() {
        return this.storeMapper.selectList(null);
    }

    @Override
    public boolean addStore(String storeName) {
        if (this.storeExists(storeName)) {
            return false;
        }
        Store store = new Store();
        store.setName(storeName);
        this.storeMapper.insert(store);
        return true;
    }

    @Override
    public Store getStoreByName(String storeName) {
        return this.storeMapper.selectById(storeName);
    }

    @Override
    public boolean storeExists(String storeName) {
        return this.getStoreByName(storeName) != null;
    }

    @Override
    public boolean deleteStore(String storeName) {
        if (!(this.storeExists(storeName))) {
            return false;
        }
        List<Goods> goodsList = this.listAllGoods(storeName);
        for (Goods goods : goodsList) {
            this.deleteGoods(goods.getId());
        }
        this.storeMapper.deleteById(storeName);
        return true;
    }

    @Override
    public Long addGoods(String storeName, String goodsName, BigDecimal price, Integer quantity) {
        if (this.getGoodsByName(storeName, goodsName) != null) {
            return null;
        }
        Goods goods = new Goods();
        goods.setName(goodsName);
        goods.setStore(storeName);
        goods.setPrice(price);
        goods.setQuantity(quantity);
        this.goodsMapper.insert(goods);
        return goods.getId();
    }

    @Override
    public List<Goods> listAllGoods(String storeName) {
        if (!this.storeExists(storeName)) {
            return null;
        }
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("store", storeName);
        return this.goodsMapper.selectList(wrapper);
    }

    @Override
    public Goods getGoodsById(Long goodsId) {
        return this.goodsMapper.selectById(goodsId);
    }

    @Override
    public Goods getGoodsByName(String storeName, String goodsName) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("store", storeName);
        wrapper.eq("name", goodsName);
        return this.goodsMapper.selectOne(wrapper);
    }

    @Override
    public boolean goodsExists(Long goodsId) {
        return this.getGoodsById(goodsId) != null;
    }

    @Override
    public boolean deleteGoods(Long goodsId) {
        if (!this.goodsExists(goodsId)) {
            return false;
        }
        List<GoodsPhoto> photos = this.listAllGoodsPhotos(goodsId);
        for (GoodsPhoto photo : photos) {
            this.deleteGoodsPhoto(photo.getId());
        }
        this.goodsMapper.deleteById(goodsId);
        return true;
    }

    @Override
    public boolean changeGoodsPrice(Long goodsId, BigDecimal newPrice) {
        Goods goods = this.getGoodsById(goodsId);
        if (goods == null) {
            return false;
        }
        goods.setPrice(newPrice);
        this.goodsMapper.updateById(goods);
        return true;
    }

    @Override
    public boolean changeGoodsQuantity(Long goodsId, Integer newQuantity) {
        Goods goods = this.getGoodsById(goodsId);
        if (goods == null) {
            return false;
        }
        goods.setQuantity(newQuantity);
        this.goodsMapper.updateById(goods);
        return true;
    }

    @Override
    public Long addGoodsPhoto(Long goodsId, String photoPath) {
        if (!this.goodsExists(goodsId)) {
            return null;
        }
        GoodsPhoto photo = new GoodsPhoto();
        photo.setGoodsId(goodsId);
        photo.setPath(photoPath);
        this.goodsPhotoMapper.insert(photo);
        return photo.getId();
    }

    @Override
    public GoodsPhoto getGoodsPhotoById(Long photoId) {
        return this.goodsPhotoMapper.selectById(photoId);
    }

    @Override
    public boolean goodsPhotoExists(Long photoId) {
        return this.getGoodsPhotoById(photoId) != null;
    }

    @Override
    public List<GoodsPhoto> listAllGoodsPhotos(Long goodsId) {
        if (!this.goodsExists(goodsId)) {
            return null;
        }
        QueryWrapper<GoodsPhoto> wrapper = new QueryWrapper<>();
        wrapper.eq("goodsId", goodsId);
        return this.goodsPhotoMapper.selectList(wrapper);
    }

    @Override
    public boolean deleteGoodsPhoto(Long photoId) {
        if (!this.goodsPhotoExists(photoId)) {
            return false;
        }
        this.goodsPhotoMapper.deleteById(photoId);
        return true;
    }
}