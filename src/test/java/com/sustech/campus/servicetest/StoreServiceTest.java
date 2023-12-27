package com.sustech.campus.servicetest;

import com.sustech.campus.entity.Goods;
import com.sustech.campus.entity.GoodsPhoto;
import com.sustech.campus.entity.Store;
import com.sustech.campus.service.StoreService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreServiceTest {
    private StoreService storeService;

    private List<Store> storeList;

    private List<Goods> goodsList;

    private List<GoodsPhoto> photoList;

    @Autowired
    public StoreServiceTest(StoreService storeService) {
        this.storeService = storeService;
        this.storeList = new ArrayList<>();
        this.goodsList = new ArrayList<>();
        this.photoList = new ArrayList<>();
    }

    @BeforeEach
    void insert() {
        int numStores = 2;
        int numGoods = 10;
        int numGoodsPhotos = 20;
        for (int i = 0; i < numStores; ++i) {
            Store store = new Store();
            store.setName("test_store_" + i);
            Long storeId = this.storeService.addStore(store.getName());
            assertNotNull(storeId);
            store.setId(storeId);
            this.storeList.add(store);
        }
        for (int i = 0; i < numGoods; ++i) {
            Goods goods = new Goods();
            goods.setName("test_goods_" + i);
            int storeId = new Random().nextInt(this.storeList.size());
            goods.setStoreId(this.storeList.get(storeId).getId());
            goods.setPrice(new BigDecimal(new Random().nextInt(10)));
            goods.setQuantity(new Random().nextInt(10));
            Long goodsId = this.storeService.addGoods(
                    goods.getStoreId(),
                    goods.getName(),
                    goods.getPrice(),
                    goods.getQuantity()
            );
            assertNotNull(goodsId);
            goods.setId(goodsId);
            goods.setHidden(false);
            this.goodsList.add(goods);
        }
        for (int i = 0; i < numGoodsPhotos; ++i) {
            GoodsPhoto photo = new GoodsPhoto();
            photo.setPath("/foo/bar/" + i);
            int goodsId = new Random().nextInt(this.goodsList.size());
            photo.setGoodsId(this.goodsList.get(goodsId).getId());
            Long photoId = this.storeService.addGoodsPhoto(
                    photo.getGoodsId(),
                    photo.getPath()
            );
            assertNotNull(photoId);
            photo.setId(photoId);
            this.photoList.add(photo);
        }
    }

    @AfterEach
    void clean() {
        for (Store store : this.storeList) {
            assertTrue(this.storeService.deleteStore(store.getId()));
        }
    }

    @Test
    @Order(1)
    void testGetStore() {
        for (Store store : this.storeList) {
            assertEquals(
                    this.storeService.getStoreByName(store.getName()),
                    store
            );
            assertEquals(
                    this.storeService.getStoreById(store.getId()),
                    store
            );
        }
    }

    @Test
    @Order(2)
    void testStoreExists() {
        for (Store store : this.storeList) {
            assertTrue(this.storeService.storeExists(store.getName()));
            assertTrue(this.storeService.storeExists(store.getId()));
        }
    }

    @Test
    @Order(3)
    void testListAllStores() {
        assertIterableEquals(
                this.storeService.listAllStores().stream()
                        .sorted(Comparator.comparing(Store::getName)).toList(),
                this.storeList.stream()
                        .sorted(Comparator.comparing(Store::getName)).toList()
        );
    }

    @Test
    @Order(4)
    void testListAllGoods() {
        for (Store store : this.storeList) {
            assertIterableEquals(
                    this.storeService.listAllGoods(store.getId()).stream()
                            .sorted(Comparator.comparing(Goods::getId)).toList(),
                    this.goodsList.stream().filter(e -> e.getStoreId().equals(store.getId()))
                            .sorted(Comparator.comparing(Goods::getId)).toList()
            );
        }
    }

    @Test
    @Order(5)
    void testGetGoodsById() {
        for (Goods goods : this.goodsList) {
            assertEquals(
                    this.storeService.getGoodsById(goods.getId()),
                    goods
            );
        }
    }

    @Test
    @Order(6)
    void testGetGoodsByName() {
        for (Goods goods : this.goodsList) {
            assertEquals(
                    this.storeService.getGoodsByName(goods.getStoreId(), goods.getName()),
                    goods
            );
        }
    }

    @Test
    @Order(7)
    void testGoodsExists() {
        for (Goods goods : this.goodsList) {
            assertTrue(this.storeService.goodsExists(goods.getId()));
        }
    }

    @Test
    @Order(8)
    void testDeleteGoods() {
        for (Goods goods : this.goodsList) {
            assertTrue(this.storeService.deleteGoods(goods.getId()));
            assertFalse(this.storeService.goodsExists(goods.getId()));
        }
    }

    @Test
    @Order(9)
    void testChangeGoodsPrice() {
        for (Goods goods : this.goodsList) {
            goods.setPrice(goods.getPrice().add(BigDecimal.ONE));
            assertTrue(this.storeService.changeGoodsPrice(
                    goods.getId(),
                    goods.getPrice()
            ));
            assertEquals(
                    0,
                    goods.getPrice().compareTo(this.storeService.getGoodsById(goods.getId()).getPrice())
            );
        }
    }

    @Test
    @Order(10)
    void testChangeGoodsQuantity() {
        for (Goods goods : this.goodsList) {
            goods.setQuantity(goods.getQuantity() + 1);
            assertTrue(this.storeService.changeGoodsQuantity(
                    goods.getId(),
                    goods.getQuantity()
            ));
            assertEquals(
                    this.storeService.getGoodsById(goods.getId()).getQuantity(),
                    goods.getQuantity()
            );
        }
    }

    @Test
    @Order(11)
    void testGetGoodsPhotoById() {
        for (GoodsPhoto photo : this.photoList) {
            assertEquals(
                    this.storeService.getGoodsPhotoById(photo.getId()),
                    photo
            );
        }
    }

    @Test
    @Order(12)
    void testGoodsPhotoExists() {
        for (GoodsPhoto photo : this.photoList) {
            assertTrue(this.storeService.goodsPhotoExists(photo.getId()));
        }
    }

    @Test
    @Order(13)
    void testListAllGoodsPhotos() {
        for (Goods goods : this.goodsList) {
            assertIterableEquals(
                    this.storeService.listAllPhotosOfaGood(goods.getId()).stream()
                            .sorted(Comparator.comparing(GoodsPhoto::getId)).toList(),
                    this.photoList.stream().filter(e -> e.getGoodsId().equals(goods.getId()))
                            .sorted(Comparator.comparing(GoodsPhoto::getId)).toList()
            );
        }
    }

    @Test
    @Order(14)
    void testDeletePhoto() {
        for (GoodsPhoto photo : this.photoList) {
            assertTrue(this.storeService.deleteGoodsPhoto(photo.getId()));
            assertFalse(this.storeService.goodsPhotoExists(photo.getId()));
        }
    }

    @Test
    @Order(15)
    void testFakeDeleteGoods() {
        for (Goods goods : this.goodsList) {
            assertTrue(this.storeService.fakeDeleteGoods(goods.getId()));
            goods.setHidden(true);
            assertEquals(
                    this.storeService.getGoodsById(goods.getId(), true),
                    goods
            );
        }
    }
}
