package hgh.project.shopping.data.response

import com.google.gson.annotations.SerializedName

data class ProductsResponse(
    @SerializedName("items")
    val item: List<ProductResponse>,
    @SerializedName("count")
    val count: Int
)
