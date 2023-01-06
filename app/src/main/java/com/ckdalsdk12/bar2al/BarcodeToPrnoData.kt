package com.ckdalsdk12.bar2al

import com.google.gson.annotations.SerializedName

data class BarcodeToPrnoData (
    @SerializedName("C005")
    val c005: C005
)

data class C005 (
    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("row")
    val row: List<BarcodeToPrnoDataRow>,

    @SerializedName("RESULT")
    val result: BarcodeToPrnoDataResult
)

data class BarcodeToPrnoDataResult (
    @SerializedName("MSG")
    val msg: String,

    @SerializedName("CODE")
    val code: String
)

data class BarcodeToPrnoDataRow (
    @SerializedName("CLSBIZ_DT")
    val clsbizDt: String,

    @SerializedName("SITE_ADDR")
    val siteAddr: String,

    @SerializedName("PRDLST_REPORT_NO")
    val prdlstReportNo: String,

    @SerializedName("PRMS_DT")
    val prmsDt: String,

    @SerializedName("PRDLST_NM")
    val prdlstNm: String,

    @SerializedName("BAR_CD")
    val barCD: String,

    @SerializedName("POG_DAYCNT")
    val pogDaycnt: String,

    @SerializedName("PRDLST_DCNM")
    val prdlstDcnm: String,

    @SerializedName("BSSH_NM")
    val bsshNm: String,

    @SerializedName("END_DT")
    val endDt: String,

    @SerializedName("INDUTY_NM")
    val indutyNm: String
)