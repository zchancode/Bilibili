package com.java.container;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.bilibili.R;
import com.example.common.autoservice.IWebViewService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ContainerActivity extends AppCompatActivity {
    String TAG = "ContainerActivity";

    ArrayList<String> mArrayList = new ArrayList<>();//查询效率高，增删效率低
    LinkedList<String> mLinkedList = new LinkedList<>();//查询效率低，增删效率高
    /*
        区别 ： ArrayList 是基于数组实现的，LinkedList 是基于链表实现的
    */

    HashSet<String> mHashSet = new HashSet<>();//不是存储顺序 无重复元素
    TreeSet<String> mTreeSet = new TreeSet<>();//是存储顺序，无重复元素

    HashMap<String, String> mHashMap = new HashMap<>();//无序的
    TreeMap<String, String> mTreeMap = new TreeMap<>();//有序的 根据key排序
    //相同key会覆盖


    //TODO 迭代器

    {

        //迭代mLinkedList
        for (String s : mLinkedList) {
            Log.e("TAG", "onCreate: " + s);
        }

        Iterator<String> iterator = mLinkedList.iterator();
        while (iterator.hasNext()) {
            Log.e("TAG", "onCreate: " + iterator.next());
        }


        //迭代HashSet
        for (String s : mHashSet) {
            Log.e("TAG", "onCreate: " + s);
        }

        //迭代TreeSet
        for (String s : mTreeSet) {
            Log.e("TAG", "onCreate: " + s);
        }

        //迭代HashMap
        Set<String> keySet = mHashMap.keySet();
        for (String s : keySet) {
            Log.e("TAG", "onCreate: " + s);
        }

        Iterator<String> map = mHashMap.keySet().iterator();
        while (map.hasNext()) {
            String key = map.next();
            Log.e("TAG", "onCreate: " + key);
        }

        //迭代TreeMap
        Set<String> keySet1 = mTreeMap.keySet();
        for (String s : keySet1) {
            Log.e("TAG", "onCreate: " + s);
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_activity_main);
        ServiceLoader<IWebViewService> serviceLoader = ServiceLoader.load(IWebViewService.class);
        if (serviceLoader.iterator().hasNext()) {
            for (IWebViewService service : serviceLoader) {
                Log.d(TAG, "Service found: " + service.getClass().getName());
                service.loadLocalPage(this, "file:///android_asset/demo.html", "title", true);
            }
        } else {
            Log.e(TAG, "No implementations for IWebViewService found");
        }

    }
}