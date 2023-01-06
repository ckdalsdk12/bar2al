package com.ckdalsdk12.bar2al

data class PrnoToALData (
    val body: Body,
    val header: Header
)

data class Body (
    val items: List<Items>,
    val totalCount: Int,
    val pageNo: String,
    val numOfRows: String
)

data class Items (
    val item: Item
)

data class Item (
    val nutrient: String,
    val rawmtrl: String,
    val prdlstNm: String,
    val imgurl2: String,
    val barcode: String,
    val imgurl1: String,
    val productGb: String,
    val seller: String,
    val prdkindstate: String,
    val rnum: String,
    val manufacture: String,
    val prdkind: String,
    val capacity: String,
    val prdlstReportNo: String,
    val allergy: String
)

data class Header (
    val resultCode: String,
    val resultMessage: String
)