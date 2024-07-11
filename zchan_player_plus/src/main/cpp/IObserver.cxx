//
// Created by Administrator on 2024-03-17.
//
#pragma once
#include <vector>
#include <mutex>
#include "Data.cxx"

class IObserver {
public:
    virtual void receiveData(Data data){};
    void addObserver(IObserver *observer){
        std::lock_guard<std::mutex> lock(mutex);
        observers.push_back(observer);
    }

    void sendData(Data data){
        std::lock_guard<std::mutex> lock(mutex);
        for (auto observer : observers){
            observer->receiveData(data);
        }
    }

private:
    std::vector<IObserver *> observers;
protected:
    std::mutex mutex;
};