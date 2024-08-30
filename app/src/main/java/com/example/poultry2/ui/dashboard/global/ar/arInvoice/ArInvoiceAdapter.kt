package com.example.poultry2.ui.dashboard.global.ar.arInvoice

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ItemArAgingBinding
import com.example.poultry2.databinding.ItemArInvoiceBinding
import com.example.poultry2.ui.function.MyDate.toDateString
import com.example.poultry2.ui.function.Utils

import java.time.LocalDate


class ArInvoiceAdapter internal constructor(
    context: Context,

    ) : BaseExpandableListAdapter() {

    private var groupList=emptyList<Data.ArAging>()
    private var dataList= HashMap<String, List<Data.ArInvoice>>()

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var groupBinding: ItemArAgingBinding
    private lateinit var itemBinding: ItemArInvoiceBinding

    var onItemClick: ((Data.ArInvoice)->Unit) ?= null



    override fun getChild(listPosition: Int, expandedListPosition: Int): Data.ArInvoice {
        return this.dataList[this.groupList[listPosition].aging]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        view: View?,
        parent: ViewGroup
    ): View {
        var convertView = view
        val holder: ItemViewHolder
        if (convertView == null) {
            itemBinding = ItemArInvoiceBinding.inflate(inflater)
            convertView = itemBinding.root

            holder = ItemViewHolder()
            holder.date=itemBinding.tvDate
            holder.invoiceNo=itemBinding.tvInvoice
            holder.balanceType=itemBinding.tvBalanceType
            holder.terms=itemBinding.tvTerms
            holder.due=itemBinding.tvDue
            holder.balance=itemBinding.tvBalance
            holder.check=itemBinding.tvCheck
            holder.collected=itemBinding.ckCollected
            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemViewHolder
        }

        val item=getChild(listPosition, expandedListPosition)

        holder.date!!.text=LocalDate.parse(item.date).toDateString("MMM dd, yyyy")
        holder.invoiceNo!!.text=item.invoiceNo
        holder.balanceType!!.text=item.balanceType
        holder.terms!!.text="Terms: ${item.terms}"
        holder.balance!!.text= Utils.formatDoubleToString(item.balance)
        holder.due!!.text="Due: ${LocalDate.parse(item.dueDate).toDateString("MMM dd, yyyy")}"
        if (item.checkNo!=""){
            holder.check!!.text="Check# ${item.checkNo} , ${LocalDate.parse(item.checkDate).toDateString("MMM dd, yyyy")}"
            holder.check!!.visibility=View.VISIBLE
        } else
            holder.check!!.visibility= View.GONE

        holder.collected!!.isChecked=item.isChecked

        holder.collected!!.setOnClickListener{
            onItemClick?.invoke(item)
        }

        return convertView
    }



    override fun getChildrenCount(listPosition: Int): Int {
        return this.dataList[this.groupList[listPosition].aging]!!.size
    }

    override fun getGroup(listPosition: Int): Data.ArAging {
        return this.groupList[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.groupList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        view: View?,
        parent: ViewGroup
    ): View {
        var convertView = view
        val holder: GroupViewHolder
        if (convertView == null) {
            groupBinding = ItemArAgingBinding.inflate(inflater)
            convertView = groupBinding.root
            holder = GroupViewHolder()

            holder.aging=groupBinding.tvAging
            holder.balance=groupBinding.tvBalance

            convertView.tag = holder
        } else {
            holder = convertView.tag as GroupViewHolder
        }

        val group= getGroup(listPosition)

        holder.aging!!.text=group.aging
        holder.balance!!.text=Utils.formatDoubleToString(group.total)
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    inner class ItemViewHolder {
        internal var date: TextView? = null
        internal var invoiceNo: TextView? = null
        internal var balanceType: TextView? = null
        internal var terms: TextView? = null
        internal var due: TextView? = null
        internal var balance: TextView? = null
        internal var check: TextView? = null
        internal var collected:CheckBox?=null
    }

    inner class GroupViewHolder {
        internal var aging:TextView?=null
        internal var balance:TextView?=null
    }

    internal fun setData(groupList: List<Data.ArAging>, dataList: HashMap<String, List<Data.ArInvoice>>) {
        this.groupList = groupList
        this.dataList=dataList
        notifyDataSetChanged()
    }

    internal fun getDataList():HashMap<String, List<Data.ArInvoice>>{
        return this.dataList
    }

}
