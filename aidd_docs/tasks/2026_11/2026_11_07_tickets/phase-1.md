---
status: in-progress
---

# Instruction: Orders and tickets in domain and data

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
domain/src/commonMain/kotlin/app/homeout/domain/
├── Event.kt                 ✏️ icon: EventIcon
├── EventIcon.kt             ✅ MUSIC_NOTES, VINYL_RECORD, FORK_KNIFE, WINE
├── PaymentMethod.kt         ✅ MB_WAY, CARD, APPLE_PAY, GOOGLE_WALLET
├── Order.kt                 ✅ eventId, quantity, payment (null when free)
├── OrderRepository.kt       ✅ orders: Flow<List<Order>>; add(order)
├── PlaceOrder.kt            ✅ validates 1..8 and payment vs free, adds the order
├── Ticket.kt                ✅ Ticket(code, number, of, payment) and TicketGroup(event, tickets)
└── GetTicketGroups.kt       ✅ Flow<List<TicketGroup>> in agenda order, codes HO-<id4>-<nnn>
domain/src/commonTest/kotlin/app/homeout/domain/
├── PlaceOrderTest.kt        ✅
├── GetTicketGroupsTest.kt   ✅
└── TestEvents.kt            ✏️ icon default
data/src/commonMain/kotlin/app/homeout/data/
├── SampleEventRepository.kt ✏️ icons from the design
├── InMemoryOrderRepository.kt ✅ MutableStateFlow-backed
└── DataModule.kt            ✏️ single<OrderRepository>
data/src/commonTest/kotlin/app/homeout/data/
├── SampleEventRepositoryTest.kt ✏️ icons
└── InMemoryOrderRepositoryTest.kt ✅
composeApp/src/commonMain/kotlin/app/homeout/di/DomainModule.kt   ✏️ PlaceOrder, GetTicketGroups
composeApp/src/commonTest/kotlin/app/homeout/di/KoinGraphTest.kt ✏️ new bindings
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Fixed clock on Friday 2026-11-13 19:00 UTC, sample events, empty in-memory orders | No orders | system |
| Happy path   | `PlaceOrder(fado, 2, GOOGLE_WALLET)` | One order. `GetTicketGroups` emits Fado with tickets `HO-FADO-001` and `HO-FADO-002`, "1 of 2" and "2 of 2", paid with Google Wallet. | system |
| Happy path   | Then `PlaceOrder(fado, 1, MB_WAY)` | Fado's group has 3 tickets numbered 1–3 of 3. The third is paid with MB WAY. | system |
| Happy path   | `PlaceOrder(night-market, 1, null)` and orders for Petiscos and Fado | Groups in agenda order (Fado, then Market, then Petiscos). The Market ticket has no payment. | system |
| Edge case    | Quantity 0 or 9 | Rejected (`IllegalArgumentException`) and no order added | system |
| Edge case    | A paid event with no payment, or a free event with one | Rejected | system |
| Edge case    | An unknown event id | Rejected | system |
| Happy path   | Sample events | Icons: Fado music notes, Vinyl vinyl record, Market fork and knife, Petiscos wine | system |
| Happy path   | `KoinGraphTest` | `OrderRepository` is a singleton (the same instance twice). `PlaceOrder` and `GetTicketGroups` resolve. | system |

## Tasks to do

### `1)` Domain

> Orders and tickets as domain rules.

1. `EventIcon`, and `Event.icon` with no default, so every source must say which icon it means. `TestEvents.testEvent` and the `Event(...)` in `KoinGraphTest` pass one.
2. `PaymentMethod`, `Order` and the `OrderRepository` interface.
3. `PlaceOrder(events, orders)`: checks the quantity is 1..8, the event exists, and a payment is given exactly when the event isn't free. Then it adds the order.
4. `GetTicketGroups(events, orders)`: flattens each event's orders in order, numbers its tickets 1..n, and builds the code `HO-${id.take(4).uppercase()}-${n.padStart(3,'0')}`. Groups follow the events' start order, and only events that have tickets appear.
5. Tests.

### `2)` Data and Koin

> An in-memory store and its bindings.

1. `InMemoryOrderRepository`, backed by a `MutableStateFlow`. Its test checks that `add` emits.
2. Icons on the sample events.
3. Bindings: `single<OrderRepository>` in `dataModule`, and factories for `PlaceOrder` and `GetTicketGroups`. `KoinGraphTest` covers them.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `PlaceOrderTest` and `GetTicketGroupsTest` pass on JVM and Wasm, and `domain` still has no DI or UI dependency |
| 2 | Data tests and `KoinGraphTest` pass, and all existing tests still pass |
