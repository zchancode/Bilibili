//
// Created by Administrator on 2024-03-25.
//
#pragma once

#include "FFData.cxx"
#include <vector>

class FFObserver {
private:
    std::vector<FFObserver *> observers;
public:
    virtual void receive(FFData data) {

    }

    void send(FFData data) {
        for (int i = 0; i < observers.size(); i++) {
            observers[i]->receive(data);
        }
    }

    void addObserver(FFObserver *observer) {
        observers.push_back(observer);
    }
};
