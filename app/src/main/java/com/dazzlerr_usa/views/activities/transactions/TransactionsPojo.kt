package com.dazzlerr_usa.views.activities.transactions

data class TransactionsPojo(
        val data: MutableList<Data> = ArrayList(),
        val status: String = "",
        val success: Boolean = false
) {
    data class Data(
            val order_date: String = "",
            val order_id: Int = 0,
            val order_total: String = "",
            var payment_method: String = "",
            var membership_name: String = ""

    )
}