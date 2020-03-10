package com.aandssoftware.aandsinventory.models

import java.io.Serializable

data class CodeValueModel(var code: String?, var value: String?) : Serializable
data class CodeValueListModel(var data: List<CodeValueModel> = ArrayList<CodeValueModel>()) : Serializable

