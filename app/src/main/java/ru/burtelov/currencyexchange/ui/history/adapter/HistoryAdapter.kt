package ru.burtelov.currencyexchange.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.burtelov.currencyexchange.data.model.History
import ru.burtelov.currencyexchange.databinding.FragmentHistoryItemBinding

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    var historyList: List<History> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = FragmentHistoryItemBinding.inflate(inflater, parent, false)

        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        val item = historyList[position]

        holder.binding.apply {
            historyCurrencyStart.text = item.from_currency_name
            historyStartPrice.text = item.from_currency_price

            historyCurrencyFinish.text = item.to_currency_name
            historyFinishPrice.text = item.to_currency_price

            historyDateExchange.text = item.date
        }

    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    class HistoryViewHolder(var binding: FragmentHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}