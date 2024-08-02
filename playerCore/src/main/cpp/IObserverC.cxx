//
// Created by Administrator on 2024-07-29.
//
#pragma once

#include <vector>
#include <mutex>
#include "DataC.cxx"

class IObserverC{
public:
    virtual void receiveData(DataC data){};
    void addObserver(IObserverC *observer){
        mutex.lock();
        observers.push_back(observer);
        mutex.unlock();
    }

    void sendData(DataC data){
        mutex.lock();
        for (auto observer : observers){
            observer->receiveData(data);
        }
        mutex.unlock();
    }

private:
    std::vector<IObserverC *> observers;

protected:
    std::mutex mutex;
};