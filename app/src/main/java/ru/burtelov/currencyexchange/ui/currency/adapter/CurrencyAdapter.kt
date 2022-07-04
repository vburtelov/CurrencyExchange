package ru.burtelov.currencyexchange.ui.currency.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.burtelov.currencyexchange.R
import ru.burtelov.currencyexchange.data.model.LikedCurrencies
import ru.burtelov.currencyexchange.databinding.FragmentCurrencyItemBinding
import ru.burtelov.currencyexchange.ui.currency.CurrencyAdapterInterface
import ru.burtelov.currencyexchange.ui.detail.DetailFragment

class CurrencyAdapter(private val currencyInterface: CurrencyAdapterInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var currencyList = mutableListOf<LikedCurrencies>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val bindingMain = FragmentCurrencyItemBinding.inflate(inflater, parent, false)
        return RecyclerViewItem.CurrencyViewHolderMain(bindingMain, currencyInterface)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecyclerViewItem.CurrencyViewHolderMain) {
            val item = currencyList[position]

            holder.itemView.setOnClickListener {
                currencyInterface.openDetailWindow()
                DetailFragment.setCurrency(item.name)
                DetailFragment.setRate(item.price)
            }
            holder.bindingMain(item)
        }
    }

    override fun getItemCount(): Int = currencyList.size

    class RecyclerViewItem {
        class CurrencyViewHolderMain(
            private val bindingMain: FragmentCurrencyItemBinding,
            private val currencyInterface: CurrencyAdapterInterface,
        ) :
            RecyclerView.ViewHolder(bindingMain.root) {
            fun bindingMain(item: LikedCurrencies) {
                bindingMain.apply {
                    currencyName.text = item.name
                    currencyValue.text = item.price.toString()
                    if (item.is_liked) currencyLiked.setImageResource(R.drawable.ic_heart_filled)
                    else currencyLiked.setImageResource(R.drawable.ic_heart)

                    currencyLiked.setOnClickListener {
                        if (item.is_liked) currencyInterface.dislikeCurrency(item.name)
                        else currencyInterface.likeCurrency(item.name)
                    }
                }
            }
        }
    }
}