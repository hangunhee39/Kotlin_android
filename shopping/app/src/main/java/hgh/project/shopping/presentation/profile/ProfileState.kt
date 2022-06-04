package hgh.project.shopping.presentation.profile

import android.net.Uri
import hgh.project.shopping.data.entity.product.ProductEntity


sealed class ProfileState{

    object Uninitialized: ProfileState()

    object Loading: ProfileState()

    data class Login(
        val idToken: String
    ): ProfileState()

    sealed class Success: ProfileState() {

        data class Registered(
            val userName: String,
            val profileImage: Uri?,
            val productList:List<ProductEntity>
        ) :Success()

        object NotRegistered: Success()
    }

    object Error: ProfileState()
}
