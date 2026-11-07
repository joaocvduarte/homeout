package app.homeout.buy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.homeout.BuyDestination
import app.homeout.domain.EventDetail
import app.homeout.domain.GetEvent
import app.homeout.domain.Order
import app.homeout.domain.PaymentMethod
import app.homeout.domain.PlaceOrder
import app.homeout.domain.Price
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** What the Buy screen shows: the event, the count (1 to 8) and, for a paid event, the method. */
data class BuyState(
    val detail: EventDetail,
    val quantity: Int,
    val method: PaymentMethod,
) {
    val free: Boolean get() = detail.event.price == Price.Free
    val canRemove: Boolean get() = quantity > 1
    val canAdd: Boolean get() = quantity < Order.MAX_TICKETS
}

/** The count and method live in saved state, so they survive recreation. */
class BuyViewModel(
    private val handle: SavedStateHandle,
    getEvent: GetEvent,
    private val placeOrder: PlaceOrder,
) : ViewModel() {
    private val id = handle.toRoute<BuyDestination>().id
    private val _state = MutableStateFlow<BuyState?>(null)

    /** Updated directly, without a combining flow, so each tap shows on the next frame. */
    val state: StateFlow<BuyState?> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = getEvent(id)?.let { BuyState(it, quantity(), method()) }
        }
    }

    fun add() = setQuantity(quantity() + 1)

    fun remove() = setQuantity(quantity() - 1)

    fun select(method: PaymentMethod) {
        handle[METHOD] = method.name
        _state.update { it?.copy(method = method) }
    }

    /** Places the order and returns the confirmation the Tickets tab shows. */
    suspend fun pay(): String {
        val current = requireNotNull(state.value) { "The event hasn't loaded." }
        placeOrder(current.detail.event.id, current.quantity, current.method.takeUnless { current.free })
        return confirmationMessage(current.free, current.quantity, current.detail.event.title)
    }

    private fun quantity(): Int = handle[QUANTITY] ?: 1

    private fun method(): PaymentMethod = PaymentMethod.valueOf(handle[METHOD] ?: PaymentMethod.MB_WAY.name)

    private fun setQuantity(quantity: Int) {
        val clamped = quantity.coerceIn(1, Order.MAX_TICKETS)
        handle[QUANTITY] = clamped
        _state.update { it?.copy(quantity = clamped) }
    }

    private companion object {
        const val QUANTITY = "quantity"
        const val METHOD = "method"
    }
}
