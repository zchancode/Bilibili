package com.example.bilibili;

import org.junit.Test;

import static org.junit.Assert.*;

import com.zchan.main.model.bean.BaseBean;
import com.zchan.main.model.bean.Goods;
import com.zchan.main.model.network.RetrofitClient;
import com.zchan.main.model.network.service.GoodsService;

import java.util.List;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getService(GoodsService.class).getGoods().subscribe(new Consumer<BaseBean<List<Goods>>>(){
            @Override
            public void accept(BaseBean<List<Goods>> listBaseBean) throws Throwable {
                System.out.println("listBaseBean = " + listBaseBean.toString());

            }
        });
        while (true){

        }
    }
}