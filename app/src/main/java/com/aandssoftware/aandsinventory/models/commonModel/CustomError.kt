package com.aandssoftware.aandsinventory.models.commonModel

data class CustomError(val id: Int, val errorType: String?, var errorMessage: String?, var showType: String?) {
    override fun equals(other: Any?): Boolean {
        val error = other as CustomError
        return this.id == error.id
    }

    override fun hashCode(): Int {
        return this.id * 100
    }
}

enum class ErrorType constructor(private val errorType: String) {
    LOCAL_VALIDATION_ERROR("LOCAL_VALIDATION_ERROR"),
    SERVER_VALIDATION_ERROR("SERVER_VALIDATION_ERROR");

    override fun toString(): String {
        return errorType
    }
}

enum class ErrorShowType constructor(private val errorShowType: String) {
    INLINE_ERROR("INLINE_ERROR"),
    SNACK_BAR("SNACK_BAR"),
    TOAST("TOAST");

    override fun toString(): String {
        return errorShowType
    }
}

enum class ErrorID {
    LENGTH_ERROR,
    MANDATORY_FIELD_ERROR,
    EMAIL_ERROR,
    MAX_MOBILE_NO,
    MIN_LENGTH
}
