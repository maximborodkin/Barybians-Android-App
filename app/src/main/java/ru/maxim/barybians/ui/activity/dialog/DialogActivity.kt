package ru.maxim.barybians.ui.activity.dialog

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.model.Message
import ru.maxim.barybians.ui.activity.base.BaseActivity
import ru.maxim.barybians.utils.isNull

class DialogActivity : BaseActivity(), DialogView {

    @InjectPresenter
    lateinit var dialogPresenter: DialogPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        if (savedInstanceState.isNull()){
            val userId = intent.getIntExtra("userId", 0)
            dialogPresenter.loadMessages(userId)
        }
    }

    override fun showMessages(messages: ArrayList<Message>) {

    }
}