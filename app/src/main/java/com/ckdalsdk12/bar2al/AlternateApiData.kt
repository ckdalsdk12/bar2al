package com.ckdalsdk12.bar2al

import com.google.gson.annotations.SerializedName

data class AlternateApiData (
    @SerializedName("C002")
    val c002: C002
)

data class C002 (
    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("row")
    val row: List<AlternateApiDataRow>,

    @SerializedName("RESULT")
    val result: AlternateApiDataResult
)

data class AlternateApiDataResult (
    @SerializedName("MSG")
    val msg: String,

    @SerializedName("CODE")
    val code: String
)

data class AlternateApiDataRow (
    @SerializedName("PRDLST_REPORT_NO")
    val prdlstReportNo: String,

    @SerializedName("PRMS_DT")
    val prmsDt: String,

    @SerializedName("LCNS_NO")
    val lcnsNo: String,

    @SerializedName("PRDLST_NM")
    val prdlstNm: String,

    @SerializedName("BSSH_NM")
    val bsshNm: String,

    @SerializedName("PRDLST_DCNM")
    val prdlstDcnm: String,

    @SerializedName("RAWMTRL_NM")
    val rawmtrlNm: String
)