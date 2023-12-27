package com.sustech.campus.controller;

import com.sustech.campus.entity.Goods;
import com.sustech.campus.entity.GoodsPhoto;
import com.sustech.campus.entity.Store;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Store>> listAllStores() {
        List<Store> stores = storeService.listAllStores();
        return ResponseEntity.ok(stores);
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<String> addStore(@RequestBody Store store) {
        Long storeId = storeService.addStore(store.getName());
        if (storeId != null) {
            return ResponseEntity.ok("Store added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Store already exists.");
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<String> deleteStore(@PathVariable Long storeId) {
        boolean success = storeService.deleteStore(storeId);
        if (success) {
            return ResponseEntity.ok("Store deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Store not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @GetMapping("/goods/{storeId}")
    public ResponseEntity<List<Goods>> listAllGoods(@PathVariable Long storeId) {
        List<Goods> goods = storeService.listAllGoods(storeId);
        if (goods != null && !goods.isEmpty()) {
            return ResponseEntity.ok(goods);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/goods/add")
    public ResponseEntity<String> addGoods(@RequestBody Goods goods) {
        Long goodsId = storeService.addGoods(goods.getStoreId(), goods.getName(), goods.getPrice(), goods.getQuantity());
        if (goodsId != null) {
            return ResponseEntity.ok("Goods added successfully. ID: " + goodsId);
        } else {
            return ResponseEntity.badRequest().body("Error adding goods.");
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/goods/delete/{goodsId}")
    public ResponseEntity<String> deleteGoods(@PathVariable Long goodsId) {
        boolean success = storeService.deleteGoods(goodsId);
        if (success) {
            return ResponseEntity.ok("Goods deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goods not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PatchMapping("/goods/change-price/{goodsId}")
    public ResponseEntity<String> changeGoodsPrice(@PathVariable Long goodsId, @RequestParam BigDecimal newPrice) {
        boolean success = storeService.changeGoodsPrice(goodsId, newPrice);
        if (success) {
            return ResponseEntity.ok("Goods price updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goods not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PatchMapping("/goods/change-quantity/{goodsId}")
    public ResponseEntity<String> changeGoodsQuantity(@PathVariable Long goodsId, @RequestParam Integer newQuantity) {
        boolean success = storeService.changeGoodsQuantity(goodsId, newQuantity);
        if (success) {
            return ResponseEntity.ok("Goods quantity updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goods not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/goods/photo/add")
    public ResponseEntity<String> addGoodsPhoto(@RequestParam Long goodsId, @RequestParam String photoPath) {
        Long photoId = storeService.addGoodsPhoto(goodsId, photoPath);
        if (photoId != null) {
            return ResponseEntity.ok("Goods photo added successfully. ID: " + photoId);
        } else {
            return ResponseEntity.badRequest().body("Error adding goods photo.");
        }
    }

    @Access(level = UserType.ADMIN)
    @GetMapping("/goods/photo/{goodsId}")
    public ResponseEntity<List<GoodsPhoto>> listAllGoodsPhotos(@PathVariable Long goodsId) {
        List<GoodsPhoto> photos = storeService.listAllGoodsPhotos(goodsId);
        if (photos != null && !photos.isEmpty()) {
            return ResponseEntity.ok(photos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/goods/photo/delete/{photoId}")
    public ResponseEntity<String> deleteGoodsPhoto(@PathVariable Long photoId) {
        boolean success = storeService.deleteGoodsPhoto(photoId);
        if (success) {
            return ResponseEntity.ok("Goods photo deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goods photo not found.");
        }
    }

}
