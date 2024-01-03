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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
    public Long addStore(String storeName) {
        if (this.storeExists(storeName)) {
            return null;
        }
        Store store = new Store();
        store.setName(storeName);
        this.storeMapper.insert(store);
        return store.getId();
    }

    @Override
    public Store getStoreById(Long storeId) {
        return this.storeMapper.selectById(storeId);
    }

    @Override
    public Store getStoreByName(String storeName) {
        QueryWrapper<Store> wrapper = new QueryWrapper<>();
        wrapper.eq("name", storeName);
        return this.storeMapper.selectOne(wrapper);
    }

    @Override
    public boolean storeExists(String storeName) {
        return this.getStoreByName(storeName) != null;
    }

    @Override
    public boolean storeExists(Long storeId) {
        return this.getStoreById(storeId) != null;
    }

    @Override
    public boolean deleteStore(Long storeId) {
        if (!(this.storeExists(storeId))) {
            return false;
        }
        List<Goods> goodsList = this.listAllGoods(storeId, true);
        for (Goods goods : goodsList) {
            this.deleteGoods(goods.getId());
        }
        this.storeMapper.deleteById(storeId);
        return true;
    }

    @Override
    public Long addGoods(Long storeId, String goodsName, BigDecimal price, Integer quantity) {
        if (this.getGoodsByName(storeId, goodsName) != null) {
            return null;
        }
        Goods goods = new Goods();
        goods.setName(goodsName);
        goods.setStoreId(storeId);
        goods.setPrice(price);
        goods.setQuantity(quantity);
        goods.setHidden(false);
        this.goodsMapper.insert(goods);
        return goods.getId();
    }

    @Override
    public boolean editGoods(Long goodsId, String goodsName, BigDecimal price, Integer quantity) {
        Goods goods = this.getGoodsById(goodsId);
        if (goods == null) {
            return false;
        }
        goods.setName(goodsName);
        goods.setPrice(price);
        goods.setQuantity(quantity);
        this.goodsMapper.updateById(goods);
        return true;
    }

    @Override
    public List<Goods> listAllGoods(Long storeId) {
        return listAllGoods(storeId, false);
    }

    @Override
    public List<Goods> listAllGoods(Long storeId, boolean includeHidden) {
        if (!this.storeExists(storeId)) {
            return null;
        }
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("storeId", storeId);
        if (!includeHidden) {
            wrapper.eq("hidden", false);
        }
        return this.goodsMapper.selectList(wrapper);
    }

    @Override
    public Goods getGoodsById(Long goodsId) {
        return getGoodsById(goodsId, false);
    }

    @Override
    public Goods getGoodsById(Long goodsId, boolean includeHidden) {
        Goods goods = this.goodsMapper.selectById(goodsId);
        if (goods == null || (goods.isHidden() && !includeHidden)) {
            return null;
        }
        return goods;
    }

    @Override
    public Goods getGoodsByName(Long storeId, String goodsName) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("storeId", storeId);
        wrapper.eq("name", goodsName);
        wrapper.eq("hidden", false);
        return this.goodsMapper.selectOne(wrapper);
    }

    @Override
    public boolean goodsExists(Long goodsId) {
        return goodsExists(goodsId, false);
    }

    @Override
    public boolean goodsExists(Long goodsId, boolean includeHidden) {
        return this.getGoodsById(goodsId, includeHidden) != null;
    }

    @Override
    public boolean fakeDeleteGoods(Long goodsId) {
        Goods goods = this.getGoodsById(goodsId);
        if (goods == null || goods.isHidden()) {
            return false;
        }
        goods.setHidden(true);
        this.goodsMapper.updateById(goods);
        return true;
    }

    @Override
    public boolean deleteGoods(Long goodsId) {
        if (!this.goodsExists(goodsId, true)) {
            return false;
        }
        List<GoodsPhoto> photos = this.listAllPhotosOfaGood(goodsId);
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
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackForClassName = {"Exception", "RuntimeException"})
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
    public List<GoodsPhoto> listAllPhotosOfaGood(Long goodsId) {
        if (!this.goodsExists(goodsId, true)) {
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
