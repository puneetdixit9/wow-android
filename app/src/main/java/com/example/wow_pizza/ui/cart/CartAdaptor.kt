import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wow_pizza.databinding.ItemCartProductBinding
import com.example.wow_pizza.CartItem

class CartAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCartProductBinding.inflate(inflater, parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class CartItemViewHolder(private val binding: ItemCartProductBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.textFoodName.text = cartItem.item_name
            binding.textFoodPrice.text = "₹${cartItem.price * cartItem.count}"
            binding.layoutQuantityControl.subItem.visibility = View.VISIBLE
            binding.layoutQuantityControl.textQuantity.text = cartItem.count.toString()
        }
    }
}
