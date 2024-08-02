package com.mvp.trading.present

import com.mvp.trading.model.ITradingModel
import com.mvp.trading.model.TradingModel
import com.mvp.trading.view.ITradingView

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class TradingPresent: ITradingPresent {

    private var view: ITradingView
    private var model: ITradingModel = TradingModel()
    constructor(view: ITradingView) {
        this.view = view
    }
    override fun calculate() {
        model.calculate {
            view.showResult(it)
        }
    }
}