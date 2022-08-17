package vn.pos365.cashiers.data.model

import android.content.ContentValues
import android.content.res.Resources
import android.database.Cursor
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import vn.pos365.cashiers.data.constants.Pos365Constants
import vn.pos365.cashiers.data.local.PreferencesHelper
import vn.pos365.cashiers.util.extension.*
import vn.pos365.cashierspos365.util.extension.getBoolean
import vn.pos365.cashierspos365.util.extension.getInt
import vn.pos365.cashierspos365.util.extension.getLong
import vn.pos365.cashierspos365.util.extension.getString
import java.io.Serializable
import java.util.*

/**
 * @author  : Pos365
 * @Skype   : chukimmuoi
 * @Mobile  : +84 167 367 2505
 * @Email   : chukimmuoi@gmail.com
 * @Website : https://cafe365.pos365.vn/
 * @Project : CashiersPos365
 * Created by chukimmuoi on 22/02/2018.
 */
data class JsonContent(
        @SerializedName("OfflineId") @Expose var offlineId: String = "",
        @SerializedName("Status") @Expose var status: Int = 2,
        @SerializedName("Discount") @Expose var discount: Double = 0.0,
        @SerializedName("TotalPayment") @Expose var totalPayment: Double = 0.0,
        @SerializedName("AmountReceive") @Expose var amountReceive: Double = 0.0,
        @SerializedName("AmountReceived") @Expose var amountReceived: Double = 0.0,
        @SerializedName("Total") @Expose var total: Double = 0.0,
        @SerializedName("OrderDetails") @Expose var orderDetails: List<Product> = emptyList(),
        @SerializedName("SoldById") @Expose var soldById: Int = 0,
        @SerializedName("ExcessCashType") @Expose var excessCashType: Int? = 0,
        @SerializedName("ExcessCash") @Expose var excessCash: Double = 0.0,
        @SerializedName("RoomId") @Expose var roomId: Int? = null,
        @SerializedName("RoomName") @Expose var roomName: String = "",
        @SerializedName("Pos") @Expose var pos: String = "",
        @SerializedName("NumberOfGuests") @Expose var numberOfGuests: Int = 0,
        @Transient @SerializedName("SyncStatus") @Expose var syncStatus: Int? = 0,
        @SerializedName("VATRates") @Expose var vatRates: String = "",
        @SerializedName("DiscountValue") @Expose var discountValue: Double = 0.0,
        @SerializedName("Voucher") @Expose var voucher: Double = 0.0,
        @SerializedName("DiscountToView") @Expose var discountToView: Any = "", // "10%"
        @SerializedName("VAT") @Expose var vat: Double = 0.0,
        @SerializedName("Description") @Expose var description: String = "", // Note
        @SerializedName("ActiveDate") @Expose var activeDate: String = "",
        @SerializedName("PartnerId") @Expose var partnerId: Int? = null, // Loi DATA tren server neu khong de null.
        @SerializedName(value="Partner", alternate=["Partner1"]) @Expose var partner: Partner? = null,
        @SerializedName("OldDebt") @Expose var oldDebt: Double = 0.0,
        @SerializedName("DiscountRatio") @Expose var discountRatio: Double = 0.0, // ""
        @Transient @SerializedName("VoucherCode") @Expose var voucherCode: Any? = null,
        @Transient @SerializedName("VoucherId") @Expose var voucherId: Any? = null,
        @SerializedName("Id") @Expose var id: Int = 0,
        @SerializedName("Code") @Expose var code: String = "",
        @Transient @SerializedName("initializingTotalPayment") @Expose var isInitializingTotalPayment: Boolean? = false,
        @SerializedName("DeliveryById") @Expose var deliveryById: Int? = null,
        @SerializedName("AccountId") @Expose var accountId: Int? = null,
        @SerializedName("ShippingCost") @Expose var shippingCost: Double? = 0.0,
        @SerializedName("ShippingCostForPartner") @Expose var shippingCostForPartner: Double = 0.0,
        @SerializedName("TotalAdditionalServices") @Expose var totalAdditionalServices: Double = 0.0,
        @SerializedName("DeliveryBy") @Expose var deliveryBy: DeliveryBy? = null,
        @SerializedName("PurchaseDate") @Expose var purchaseDate: String = "",
        @SerializedName("PriceBookId") @Expose var priceBookId: String? = "0",
        @Transient @SerializedName("Topping") @Expose var topping: String? = "",
        @SerializedName("PointToValue") @Expose var pointToValue: Float = 0F,
        @SerializedName("MoreAttributes") @Expose var moreAttributes: String = "",
        @Transient @SerializedName("MoreAttributesLocal") @Expose var moreAttributesLocal: MoreAttributes = MoreAttributes(),
        @SerializedName("Printed") @Expose var printed: Boolean? = false,
        @SerializedName("ChannelId") @Expose var channelId: Int? = null,
        @Transient @SerializedName("CardNumber") @Expose var cardNumber: String? = null): Serializable {

    override fun toString(): String {
        val gson = GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create()
                .toJson(this)
        return gson.toString()
    }

    fun totalDisplay(resources: Resources , isRound: Boolean = true): String {
        total = realPrice(resources, isRound)
        return total.numberFormat()
    }

    val safePartnerId: Int
        get() = if (partnerId == null) 0 else partnerId as Int

    val safePriceBookId: Int
        get() = if (priceBookId.isNullOrEmpty()) 0 else priceBookId!!.toInt()

    val safeShippingCost: Double
        get() = if (shippingCost == null) 0.0 else shippingCost!!.toDouble()

    val safeRoomId: Int
        get() = if (roomId == null) 0 else roomId!!

    fun realPrice(resources: Resources, isRound: Boolean = true): Double {
        val sum = orderDetails.map { it.getPriceIsTimeBlock(resources, isRound, this) }.sum()
        return if (sum >= 0.0) sum else 0.0
    }

    fun realPriceWithoutPercentProduct(resources: Resources, isRound: Boolean = true): Double {
        val sum = orderDetails.filter { !it.isPercentageOfTotalOrder }.map { it.getPriceIsTimeBlock(resources, isRound) }.sum()
        return if (sum >= 0.0) sum else 0.0
    }

    fun getTotalCount(): Double {
        return if (orderDetails.isNullOrEmpty()) {
            0.0
        } else {
            orderDetails.map { it.quantity }.sum()
        }
    }

    fun getTotalLabelPrinted(): Double {
        return if (orderDetails.isNullOrEmpty()) {
            0.0
        } else {
            orderDetails.map { it.labelPrinted ?: 0.0 }.sum()
        }
    }

    fun getTotalPrintCount(): Double {
        return if (orderDetails.isNullOrEmpty()) {
            0.0
        } else {
            orderDetails.filter { !(it.getPriceConfig().dontPrintLabel || it.isTimer) }
                    .map { it.quantity - (it.labelPrinted ?: 0.0) }
                    .sum()
        }
    }

    fun clearData() {
        orderDetails = emptyList()
        discount = 0.0
        discountValue = 0.0
        discountRatio = 0.0
        voucher = 0.0
        moreAttributes = ""
        moreAttributesLocal = MoreAttributes()
        vatRates = ""
        vat = 0.0
        shippingCostForPartner = 0.0
        //priceBookId = "0"
        shippingCost = 0.0
        totalAdditionalServices = 0.0
        totalPayment = 0.0
        amountReceived = 0.0
        excessCash = 0.0
        excessCashType = null
        total = 0.0
        deliveryBy = null
        deliveryById = null
        partner = null
        partnerId = null
        code = ""
        activeDate = ""
        purchaseDate = ""
        accountId = null
        description = ""
        printed = false
        channelId = null
        cardNumber = null
        status = 2
    }

    fun convertMoreAttributesToObject(): JsonContent {
        try {
            moreAttributesLocal =
                    if (moreAttributes.isNullOrEmpty()) MoreAttributes()
                    else GsonBuilder().create()
                            .fromJson(moreAttributes, object : TypeToken<MoreAttributes>() {}.type)
                            as MoreAttributes
        } catch (e : Exception) {
            moreAttributesLocal = MoreAttributes()
        }
        return this
    }

    fun convertMoreAttributesLocalToString(): JsonContent {
        //sumVoucher()
        moreAttributes = moreAttributesLocal.toString()
        return this
    }

    fun sumVoucher(totalBefore: Double) {
       voucher = moreAttributesLocal.vouchers.sumByDouble {
           if (it.isPercent)
               totalBefore / 100 * it.value
           else
               it.value
       }
    }

    fun sumDiscount(): Double = voucher + moreAttributesLocal.pointDiscountValue

    fun calculateAdditionalServices(totalAfterShipCost: Double) {
        moreAttributesLocal.additionalServices?.map {
            it.calculateTotal(totalAfterShipCost)
        }
    }

    fun sumAdditionalServices(): Double {
        totalAdditionalServices = if (moreAttributesLocal.additionalServices == null) 0.0 else moreAttributesLocal.additionalServices!!.sumByDouble { it.total }
        return totalAdditionalServices
    }

    fun getVATMethod(): Int {
        val totalOrderDetailsNotVat = totalOrderDetail(false)
        val checkValue = total + discount - totalOrderDetailsNotVat - totalAdditionalServices - vat

        return if (vat > 0 && vatRates.isNotEmpty() && vatRates != "0") 0
        else if (checkValue >= 0) 1 else 2
    }

//    fun setPaymentMethod(accountId: Int? = 0, value: Double? = 0, index: Int = 0) {
//        try {
//            moreAttributesLocal.paymentMethods?.let {
//                it[index].accountId = paymentMethod.accountId
//                it[index].value = paymentMethod.value
//            }
//            convertMoreAttributesLocalToString()
//        } catch (e: Exception) { }
//    }

    fun calculateAmountReceive(giveBackMoney: Boolean = false) {
        excessCashType = if (giveBackMoney) 0 else 1
        amountReceived = if (moreAttributesLocal.paymentMethods.isNullOrEmpty()) {
            moreAttributesLocal.paymentMethods = listOf(PaymentMethod(null, total))
            total
        } else
            moreAttributesLocal.paymentMethods?.sumByDouble { it.value ?: 0.0 } ?: 0.0
        totalPayment = if (giveBackMoney && total < amountReceived) total else amountReceived
        excessCash = amountReceived - totalPayment
        convertMoreAttributesLocalToString()
    }

    fun calculateBeforePay(giveBackMoney: Boolean = false, caculator: (JsonContent) -> Unit) {
        if(orderDetails.find { it.isTimer && !it.stopTimer} != null) caculator(this)

        excessCashType = if (giveBackMoney) 0 else 1
        amountReceived = if (moreAttributesLocal.paymentMethods.isNullOrEmpty()) {
            moreAttributesLocal.paymentMethods = listOf(PaymentMethod(null, total))
            total
        } else
            moreAttributesLocal.paymentMethods?.sumByDouble { it.value ?: 0.0 } ?: 0.0
        totalPayment = if (giveBackMoney && total < amountReceived) total else amountReceived
        excessCash = amountReceived - totalPayment
        amountReceive = amountReceived

        if (giveBackMoney) {
            val totalOthersAccount = moreAttributesLocal.paymentMethods?.filterIndexed { i, _ -> i != 0 }
                    ?.sumByDouble { it.value ?: 0.0 } ?: 0.0
            if (amountReceived >= total)
                moreAttributesLocal.paymentMethods?.get(0)?.value =  if (totalOthersAccount >= total) 0.0 else total - totalOthersAccount
        }
        convertMoreAttributesLocalToString()
    }

    fun calculateBeforeQuickPay() {
        totalPayment = total
        amountReceived = total
        amountReceived = total
        excessCash = 0.0
        moreAttributesLocal.paymentMethods = listOf(PaymentMethod(null, total))

        convertMoreAttributesLocalToString()
    }

    fun totalOrderDetail(priceIncludeVAT: Boolean = false): Double {
        val sumOrderDetails = orderDetails.sumByDouble { it.price * it.quantity }
        return if (priceIncludeVAT)
            sumOrderDetails - vat
        else
            sumOrderDetails
    }

    fun convertToQuantityAvailabilities() = QuantityAvailabilityCheck(
            orderDetails.map { QuantityAvailability(it.safeId, it.quantity) }
    )
}

data class TotalTime (
    @SerializedName("IsCustom") @Expose var isIsCustom: Boolean = false,
    @SerializedName("Text") @Expose var text: String = "") : Serializable

data class ServerEvents(
        @SerializedName(COLUMN_ROOM_ID) @Expose val roomId: Int = 0,
        @SerializedName(COLUMN_POSITION) @Expose val position: String = "",
        @SerializedName(COLUMN_VERSION) @Expose var version: Int = 1,
        @SerializedName(COLUMN_JSON_CONTENT) @Expose var jsonContentString: String = "{}",
        @SerializedName(COLUMN_PARTITION_KEY) @Expose var partitionKey: String = "",
        @SerializedName(COLUMN_ROW_KEY) @Expose var rowKey: String = "",
        @SerializedName(COLUMN_TIMESTAMP) @Expose var timestamp: String = "",
        @SerializedName(COLUMN_E_TAG) @Expose var eTag: String = "",
        @SerializedName("Compress") @Expose var compress: Boolean = false,
        @Transient @SerializedName(COLUMN_JSON_CONTENT_OBJECT) @Expose var jsonContent: JsonContent = JsonContent(offlineId = UUID.randomUUID().toString()),
        @SerializedName("RoomName") @Expose var roomName: String = "",
        @Transient @SerializedName(COLUMN_IS_SEND) @Expose var isSend: Boolean = true) : Serializable {

    companion object {
        const val TABLE_NAME = "pos365_server_event"
        const val COLUMN_ROOM_ID = "RoomId"
        const val COLUMN_POSITION = "Position"
        const val COLUMN_VERSION = "Version"
        const val COLUMN_JSON_CONTENT = "JsonContent"
        const val COLUMN_JSON_CONTENT_OBJECT = "JsonContentObject"
        private const val COLUMN_PARTITION_KEY = "PartitionKey"
        private const val COLUMN_ROW_KEY = "RowKey"
        private const val COLUMN_TIMESTAMP = "Timestamp"
        private const val COLUMN_E_TAG = "ETag"
        const val COLUMN_IS_SEND = "IsSend"

        const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME(
                $COLUMN_ROOM_ID INTEGER,
                $COLUMN_POSITION TEXT NOT NULL,
                $COLUMN_VERSION INTEGER,
                $COLUMN_JSON_CONTENT TEXT NOT NULL,
                $COLUMN_PARTITION_KEY TEXT NOT NULL,
                $COLUMN_ROW_KEY TEXT NOT NULL,
                $COLUMN_TIMESTAMP TEXT NOT NULL,
                $COLUMN_E_TAG TEXT NOT NULL,
                $COLUMN_IS_SEND INTEGER,
                FOREIGN KEY($COLUMN_ROOM_ID) REFERENCES ${Room.TABLE_NAME}(${Room.COLUMN_ID})
            );
        """

        const val CREATE_UNIQUE = """
            CREATE UNIQUE INDEX ${TABLE_NAME}_idx ON $TABLE_NAME($COLUMN_ROOM_ID, $COLUMN_POSITION);
        """

        fun toContentValues(serverEvents: ServerEvents): ContentValues {
            val values = ContentValues()
            values.put(COLUMN_ROOM_ID, serverEvents.roomId)
            values.put(COLUMN_POSITION, serverEvents.position)
            values.put(COLUMN_VERSION, serverEvents.version)
            values.put(COLUMN_JSON_CONTENT, serverEvents.jsonContentString.replace("\"Partner\":\"", "\"PartnerError\":\""))
            values.put(COLUMN_PARTITION_KEY, serverEvents.partitionKey)
            values.put(COLUMN_ROW_KEY, serverEvents.rowKey)
            values.put(COLUMN_TIMESTAMP, serverEvents.timestamp)
            values.put(COLUMN_E_TAG, serverEvents.eTag)
            values.put(COLUMN_IS_SEND, if (serverEvents.isSend) 1 else 0)
            return values
        }

        fun parseCursor(cursor: Cursor): ServerEvents {
            return ServerEvents(
                    roomId = cursor.getInt(COLUMN_ROOM_ID),
                    position = cursor.getString(COLUMN_POSITION),
                    version = cursor.getInt(COLUMN_VERSION),
                    jsonContentString = cursor.getString(COLUMN_JSON_CONTENT),
                    partitionKey = cursor.getString(COLUMN_PARTITION_KEY),
                    rowKey = cursor.getString(COLUMN_ROW_KEY),
                    timestamp = cursor.getString(COLUMN_TIMESTAMP),
                    eTag = cursor.getString(COLUMN_E_TAG),
                    jsonContent = convertStringToJsonContent(cursor.getString(COLUMN_JSON_CONTENT)),
                    isSend = cursor.getInt(COLUMN_IS_SEND) != 0
            )
        }

        fun convertStringToJsonContent(jsonContentString: String): JsonContent {
            var mJsonContentString = jsonContentString
            if (mJsonContentString.isNullOrEmpty()) mJsonContentString = "{}"

            return try {
                GsonBuilder().create().fromJson(
                        mJsonContentString,
                        object : TypeToken<JsonContent>() {}.type
                ) as JsonContent
            } catch (e: Exception) {
                if (e.message != null && e.message!!.contains("MoreAttributes")) {
                    mJsonContentString = jsonContentString.convertJsonObjectToString("MoreAttributes")
                    GsonBuilder().create().fromJson(
                            mJsonContentString,
                            object : TypeToken<JsonContent>() {}.type
                    ) as JsonContent
                }
                else
                    GsonBuilder().create().fromJson(
                            "{}",
                            object : TypeToken<JsonContent>() {}.type
                    ) as JsonContent
            }
        }

        fun convertStringToObject(content: String) =
                GsonBuilder().create().fromJson(
                        content,
                        object : TypeToken<ServerEvents>() {}.type
                ) as ServerEvents
    }

    fun getRoomObject() = Room(id = roomId, name = jsonContent.roomName)

    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }
}

data class SignalRResponse(
        @SerializedName("H") @Expose var h: String = "",
        @SerializedName("M") @Expose var m: String = ""
) {
    companion object {
        const val TYPE_NOTIFY = "ShowNotify"
    }
}

data class Notify(
        @SerializedName("H") @Expose var h: String = "",
        @SerializedName("M") @Expose var m: String = "",
        @SerializedName("A") @Expose var message: List<String> = emptyList()) {

    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }
}

data class NotifyDetail(
        @SerializedName("Room") @Expose var h: String = "",
        @SerializedName("Code") @Expose var m: String = "",
        @SerializedName("Message") @Expose var message: String = "",
        @SerializedName("Extras") @Expose var extras: List<String> = emptyList()) {

    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }
}

data class RealTime(
        @SerializedName("H") @Expose var h: String = "",
        @SerializedName("M") @Expose var m: String = "",
        @SerializedName("A") @Expose var serverEventList: List<ServerEvents> = emptyList()) {

    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }

    fun checkUpdateRealTime(): Boolean {
        return m == "Delete" || m == "Update" || m == "UpdateSelf"
    }
}

data class ServeEntities(
        @SerializedName("BasePrice") @Expose var basePrice: Double = 0.0,
        @SerializedName("Description") @Expose var description: String = "",
        @SerializedName("Code") @Expose var code: String = "",
        @SerializedName("Name") @Expose var name: String = "",
        @SerializedName("OrderQuickNotes") @Expose var orderQuickNotes: List<String> = emptyList(),
        @SerializedName("Position") @Expose var position: String = "",
        @SerializedName("Price") @Expose var price: Double = 0.0,
        @SerializedName("Printer") @Expose var printer: String = "",
        @SerializedName("SecondPrinter") @Expose var secondPrinter: String = "",
        @SerializedName("Printer3") @Expose var printer3: String = "",
        @SerializedName("Printer4") @Expose var printer4: String = "",
        @SerializedName("Printer5") @Expose var printer5: String = "",
        @SerializedName("ProductId") @Expose var productId: Int = 0,
        @SerializedName("Quantity") @Expose var quantity: Double = 0.0,
        @SerializedName("RoomId") @Expose var roomId: Int = 0,
        @SerializedName("RoomName") @Expose var roomName: String = "",
        @SerializedName("Serveby") @Expose var serveBy: Double = 0.0,
        @SerializedName("ServebyName") @Expose var servebyName: String = "",
        @SerializedName("Topping") @Expose var topping: String = "",
        @SerializedName("TotalTopping") @Expose var totalTopping: Double = 0.0,
        @SerializedName("IsLargeUnit") @Expose var isLargeUnit: Boolean = false,
        @SerializedName("Unit") @Expose var unit: String = "",
        @SerializedName("UnitPrice") @Expose var unitPrice: Double = 0.0,
        @SerializedName("LargeUnit") @Expose var largeUnit: String = "",
        @SerializedName("PriceLargeUnit") @Expose var priceLargeUnit: Double = 0.0,
        @Transient @SerializedName("IsTimer") @Expose var isTimer: Boolean = false,
        @Transient @SerializedName("IsSplitForSalesOrder") @Expose var isSplitForSalesOrder: Boolean = false
) : Serializable {
    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }

//    val priceAndQuantityDisplay: String
//        get() = if (isLargeUnit) {
//            """<font color=${Pos365Constants.VALUE_PRICE_COLOR}>${(price + totalTopping).numberFormat()}</font> x <font color=${Pos365Constants.VALUE_QUANTITY_COLOR}>$quantityDisplay</font> <font color=${Pos365Constants.VALUE_UNIT_COLOR}>$largeUnit</font> $unit"""
//        } else {
//            """<font color=${Pos365Constants.VALUE_PRICE_COLOR}>${(price + totalTopping).numberFormat()}</font> x <font color=${Pos365Constants.VALUE_QUANTITY_COLOR}>$quantityDisplay</font> <font color=${Pos365Constants.VALUE_UNIT_COLOR}>$unit</font> $largeUnit"""
//        }

    val priceAndQuantityDisplay: String
        get() = if (isLargeUnit) {
            """<font color=${Pos365Constants.VALUE_PRICE_COLOR}>${(price + totalTopping).numberFormat()}</font> x"""
        } else {
            """<font color=${Pos365Constants.VALUE_PRICE_COLOR}>${(price + totalTopping).numberFormat()}</font> x"""
        }

    val sumPriceDisplay: String
        get() = """${((price + totalTopping) * quantity).numberFormat()}"""

    val replyQuantityDisplay: String
        get() = (quantity * -1).formatQuantity()

    val quantityDisplay: String
        get() = quantity.formatQuantity()

    var isChooseState: Boolean = false

    fun cloneWaitForConfirmation(partitionKey: String) = WaitForConfirmation(
            productId = productId,
            name = name,
            quantity = quantity,
            serveby = serveBy,
            servebyName = servebyName,
            roomId = roomId,
            roomName = roomName,
            printer = printer,
            position = position,
            createdDate = Date().convertToStringTimestampFormat(),
            totalTopping = totalTopping,
            partitionKey = partitionKey,
            rowKey = "${roomId}_$position",
            timestamp = Date().convertToStringTimestampFormat(),
            description = description,
            secondPrinter = secondPrinter,
            topping = topping)
}

data class SendServeEntities(
        @SerializedName("ServeEntities") @Expose val serverEventList: List<ServeEntities>) : Serializable {
    companion object {
        const val TABLE_NAME = "pos365_send_serve_entities"
        val UUID_OFFLINE = "SendServeEntitiesUUID".MD5()

        fun convertStringToObject(content: String) = GsonBuilder().create().fromJson(content, object : TypeToken<SendServeEntities>() {}.type) as SendServeEntities
    }

    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }
}

data class DeliveryBy (
        @SerializedName("Id") @Expose var id: Int = 0,
        @SerializedName("Code") @Expose var code: String = "",
        @SerializedName("Name") @Expose var name: String = "",
        @SerializedName("Type") @Expose var type: Int = 0,
        @SerializedName("Gender") @Expose var gender: Int = 0,
        @SerializedName("TotalDebt") @Expose var totalDebt: Int = 0,
        @SerializedName("Loyalty") @Expose var loyalty: Int = 0,
        @SerializedName("CreatedBy") @Expose var createdBy: Int = 0,
        @SerializedName("CreatedDate") @Expose var createdDate: String = "",
        @SerializedName("RetailerId") @Expose var retailerId: Int = 0,
        @SerializedName("Password") @Expose var password: String = "",
        @SerializedName("Point") @Expose var point: Int = 0,
        @SerializedName("AccountingTransactions") @Expose var accountingTransactions: List<Any> = emptyList(),
        @SerializedName("PartnerBranches") @Expose var partnerBranches: List<Any> = emptyList(),
        @SerializedName("PartnerGroupMembers") @Expose var partnerGroupMembers: List<Any> = emptyList(),
        @SerializedName("PurchaseOrders") @Expose var purchaseOrders: List<Any> = emptyList(),
        @SerializedName("Returns") @Expose var returns: List<Any> = emptyList(),
        @SerializedName("PurchaseOrderReturns") @Expose var purchaseOrderReturns: List<Any> = emptyList(),
        @SerializedName("Orders") @Expose var orders: List<Any> = emptyList(),
        @SerializedName("Orders1") @Expose var orders1: List<Any> = emptyList(),
        @SerializedName("PointUses") @Expose var pointUses: List<Any> = emptyList(),
        @SerializedName("ProductPartners") @Expose var productPartners: List<Any> = emptyList()
)

data class PointConfig (
        @SerializedName("Id") @Expose var id: Int = 0,
        @SerializedName("CreatedBy") @Expose var createdBy: Int = 0,
        @SerializedName("CreatedDate") @Expose var createdDate: String = "",
        @SerializedName("ModifiedBy") @Expose var modifiedBy: Int = 0,
        @SerializedName("ModifiedDate") @Expose var modifiedDate: String = "",
        @SerializedName("RetailerId") @Expose var retailerId: Int = 0,
        @SerializedName("ValueToPoint") @Expose var valueToPoint: Double = 0.0,
        @SerializedName("PointToValue") @Expose var pointToValue: Float = 0F,
        @SerializedName("ExcludeOrderDiscount") @Expose var excludeOrderDiscount: Boolean = false,
        @SerializedName("ExcludeProductDiscount") @Expose var excludeProductDiscount: Boolean = false
){
    companion object{
        fun convertFromString(string: String) : PointConfig {
            return if (string.isEmpty()) PointConfig()
            else GsonBuilder().create()
                    .fromJson(string, object : TypeToken<PointConfig>() {}.type)
                    as PointConfig
        }
    }

    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }
}

data class PaymentData (
        @SerializedName("Order") @Expose var order: JsonContent? = null,
        @SerializedName(COLUMN_EXCESS_CASH_TYPE) @Expose var excessCashType: Int = 0,
        @SerializedName(COLUMN_DONT_SET_TIME) @Expose var dontSetTime: Boolean = false,
        @SerializedName(COLUMN_DUPLICATE) @Expose var duplicate: String = "",
        @Transient @SerializedName(COLUMN_ID) @Expose var id: String = "",
        @Transient @SerializedName(COLUMN_HOST_NAME) @Expose var hostName: String = "",
        @Transient @SerializedName(COLUMN_BRANCH_ID) @Expose var branchId: Int = 0,
        @Transient @SerializedName(COLUMN_SYNC_COUNT) @Expose var syncCount: Int = 0,
        @SerializedName("QrCodeEnable") @Expose var qrCodeEnable: Boolean = false,
        @SerializedName("MerchantCode") @Expose var merchantCode: String = "",
        @SerializedName("MerchantName") @Expose var merchantName: String = "") : Serializable {

    companion object {
        const val TABLE_NAME = "pos365_payment_data"
        const val COLUMN_ID = "Id"
        const val COLUMN_ORDER = "Orders"
        private const val COLUMN_EXCESS_CASH_TYPE = "ExcessCashType"
        private const val COLUMN_DONT_SET_TIME = "DontSetTime"
        private const val COLUMN_DUPLICATE = "Duplicate"
        const val COLUMN_HOST_NAME = "HostName"
        const val COLUMN_BRANCH_ID = "BranchId"
        const val COLUMN_SYNC_COUNT = "SyncCount"
        const val MAX_SYNC_COUNT = 9

        const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME(
                $COLUMN_ID TEXT NOT NULL PRIMARY KEY,
                $COLUMN_ORDER TEXT NOT NULL,
                $COLUMN_EXCESS_CASH_TYPE INTEGER,
                $COLUMN_DONT_SET_TIME NUMERIC,
                $COLUMN_HOST_NAME TEXT NOT NULL,
                $COLUMN_BRANCH_ID INTEGER,
                $COLUMN_SYNC_COUNT INTEGER,
                $COLUMN_DUPLICATE TEXT NOT NULL
            );
        """

        fun toContentValues(paymentData: PaymentData, preferencesHelper: PreferencesHelper): ContentValues {
            val values = ContentValues()
            values.put(COLUMN_ID, paymentData.order?.code ?: "OFFLINE00000000")
            values.put(COLUMN_ORDER, paymentData.order?.toString() ?: "{}")
            values.put(COLUMN_EXCESS_CASH_TYPE, paymentData.excessCashType)
            values.put(COLUMN_DUPLICATE, paymentData.duplicate)
            values.put(COLUMN_DONT_SET_TIME, paymentData.dontSetTime)
            values.put(COLUMN_HOST_NAME, preferencesHelper.getHostName())
            values.put(COLUMN_BRANCH_ID, preferencesHelper.getCurrentBranchId())
            values.put(COLUMN_SYNC_COUNT, 0)
            return values
        }

        fun parseCursor(cursor: Cursor): PaymentData {
            return PaymentData(
                    id = cursor.getString(COLUMN_ID),
                    order = ServerEvents.convertStringToJsonContent(cursor.getString(COLUMN_ORDER)),
                    excessCashType = cursor.getInt(COLUMN_EXCESS_CASH_TYPE),
                    duplicate = cursor.getString(COLUMN_DUPLICATE),
                    dontSetTime = cursor.getBoolean(COLUMN_DONT_SET_TIME),
                    hostName = cursor.getString(COLUMN_HOST_NAME),
                    branchId = cursor.getInt(COLUMN_BRANCH_ID),
                    syncCount = cursor.getInt(COLUMN_SYNC_COUNT)
            )
        }

        fun getNewDuplicate() = UUID.randomUUID().toString()
    }

    val createdDateDisplay: String
        get() = if (order?.purchaseDate.isNullOrEmpty()) "" else order?.purchaseDate?.convertTimeZoneToString() ?: ""

    val createSumPriceDisplay: String
        get() = order?.total?.numberFormat() ?: ""

    override fun toString(): String {
        val gson = GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create()
                .toJson(this)
        return gson.toString()
    }

    fun checkIsBarcode(): Boolean {
        return  order?.accountId == Pos365Constants.ACCOUNT_ID_BARCODE
    }
}

data class PaymentSendMessage (
        @SerializedName("Title") @Expose var title: String = "",
        @SerializedName("Body") @Expose var body: String = "")

data class PayResult (
        @SerializedName("Message") @Expose var message: String = "",
        @SerializedName("Id") @Expose var id: Long = 0,
        @SerializedName("Code") @Expose var code: String = "",
        @SerializedName("QRCode") @Expose var qrCode: String = "",
        @SerializedName("Status") @Expose var status: Boolean = false,
        @Transient @SerializedName("JsonContent") @Expose var jsonContent: JsonContent? = null,
        @Transient @SerializedName(COLUMN_HOST_NAME) @Expose var hostName: String = "",
        @Transient @SerializedName(COLUMN_BRANCH_ID) @Expose var branchId: Int = 0,
        @Transient @SerializedName(COLUMN_TYPE) @Expose var type : Int = 0) : Serializable {

    companion object {
        const val TABLE_NAME             = "pos365_pay_result"
        const val COLUMN_ID              = "Id"
        private const val COLUMN_MESSAGE = "Message"
        private const val COLUMN_CODE    = "Code"
        private const val COLUMN_QRCODE  = "QRCode"
        private const val COLUMN_JSONCONTENT  = "JsonContent"
        const val COLUMN_LOCAL_INDEX  = "LocalIndex"
        const val COLUMN_STATUS    = "Status"
        const val COLUMN_HOST_NAME = "HostName"
        const val COLUMN_BRANCH_ID = "BranchId"
        const val COLUMN_TYPE      = "Type"

        const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME(
                $COLUMN_LOCAL_INDEX INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ID INTEGER NOT NULL,
                $COLUMN_MESSAGE TEXT NOT NULL,
                $COLUMN_CODE TEXT NOT NULL,
                $COLUMN_QRCODE TEXT NOT NULL,
                $COLUMN_JSONCONTENT TEXT,
                $COLUMN_STATUS NUMERIC,
                $COLUMN_HOST_NAME TEXT NOT NULL,
                $COLUMN_BRANCH_ID INTEGER,
                $COLUMN_TYPE INTEGER
            );
        """

        fun toContentValues(paymentResult: PayResult, preferencesHelper: PreferencesHelper): ContentValues {
            val values = ContentValues()
            values.put(COLUMN_ID, paymentResult.id)
            values.put(COLUMN_MESSAGE, paymentResult.message)
            values.put(COLUMN_CODE, paymentResult.code)
            values.put(COLUMN_QRCODE, paymentResult.qrCode)
            values.put(COLUMN_JSONCONTENT, paymentResult.jsonContent.toString())
            values.put(COLUMN_STATUS, paymentResult.status)
            values.put(COLUMN_HOST_NAME, preferencesHelper.getHostName())
            values.put(COLUMN_BRANCH_ID, preferencesHelper.getCurrentBranchId())
            values.put(COLUMN_TYPE, paymentResult.jsonContent?.accountId)
            return values
        }

        fun parseCursor(cursor: Cursor): PayResult {
            return PayResult(
                    id = cursor.getLong(COLUMN_ID),
                    message = cursor.getString(COLUMN_MESSAGE),
                    code = cursor.getString(COLUMN_CODE),
                    qrCode = cursor.getString(COLUMN_QRCODE),
                    jsonContent = ServerEvents.convertStringToJsonContent(cursor.getString(COLUMN_JSONCONTENT)),
                    status = cursor.getBoolean(COLUMN_STATUS),
                    hostName = cursor.getString(COLUMN_HOST_NAME),
                    branchId = cursor.getInt(COLUMN_BRANCH_ID)
            )
        }
    }
}

data class ResultMultiPayment (
        @SerializedName("OrderId") @Expose var orderId: Long,
        @SerializedName("Status") @Expose var status: Boolean)

data class MoreAttributes(
        @SerializedName("PointDiscount") @Expose var pointDiscount: Double = 0.0,
        @SerializedName("PointDiscountValue") @Expose var pointDiscountValue: Double = 0.0,
        @SerializedName("TemporaryPrints") var temporaryPrints: List<TemporaryPrint> = emptyList(),
        @SerializedName("Vouchers") @Expose var vouchers: List<Voucher> = emptyList(),
        @SerializedName("PaymentMethods") @Expose var paymentMethods: List<PaymentMethod>? = emptyList(),
        @SerializedName("AdditionalServices") @Expose var additionalServices: List<AdditionalService>? = emptyList() ) {

    companion object{
        fun convertFromString(string: String) : MoreAttributes {
            try {
                return if (string.isNullOrEmpty()) MoreAttributes()
                else GsonBuilder().create()
                        .fromJson(string, object : TypeToken<MoreAttributes>() {}.type)
                        as MoreAttributes
            } catch (e: Exception){
                return MoreAttributes()
            }
        }
    }

    override fun toString(): String {
        val gson = GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create()
                .toJson(this)
        return gson.toString()
    }
}

data class PaymentMethod(
        @SerializedName("AccountId") @Expose var accountId: Int? = null,
        @SerializedName("Value") @Expose var value: Double? = 0.0)

data class TemporaryPrint(
    @SerializedName("CreatedDate") var createdDate: String = "",
    @SerializedName("Total") val total: Double = 0.0) {

    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }
}

data class Voucher(
        @SerializedName("Id") @Expose var id: Long = 0,
        @SerializedName("Code") @Expose var code: String = "",
        @SerializedName("Value") @Expose var value: Double = 0.0,
        @SerializedName("Status") @Expose var status: Int = 0,
        @SerializedName("RetailerId") @Expose var retailerId: Long = 0,
        @SerializedName("CreatedDate") @Expose var createdDate: String = "",
        @SerializedName("CreatedBy") @Expose var createdBy: Long = 0,
        @SerializedName("IsPercent") @Expose var isPercent: Boolean = false,
        @Transient @SerializedName("IsChooseState") @Expose var isChooseState: Boolean = false) {

    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }
}

data class AdditionalService(
    @SerializedName("Name") @Expose var name: String = "",
    @SerializedName("Total") @Expose var total: Double = 0.0,
    @SerializedName("Value") @Expose var value: Double = 0.0,
    @SerializedName("IsPercent") @Expose var isPercent: Boolean = false
) {
    override fun toString(): String {
        val gson = GsonBuilder().create().toJson(this)
        return gson.toString()
    }

    fun calculateTotal(totalAfterShipCost: Double) {
        total = if (isPercent)
            totalAfterShipCost / 100 * value
        else
            value
    }
}

data class ErrorResponse(
        @SerializedName("ResponseStatus") val responseStatus: PayResponseStatus = PayResponseStatus()
) : Serializable {
    companion object {
        fun convertStringToObject(content: String): ErrorResponse {
            return try {
                GsonBuilder().create().fromJson(
                        content,
                        object : TypeToken<ErrorResponse>() {}.type
                ) as ErrorResponse
            } catch (e: java.lang.Exception) {
                ErrorResponse()
            }
        }
    }
}

data class PayResponseStatus(
        @SerializedName("ErrorCode") @Expose val errorCode: String = "",
        @SerializedName("Message") @Expose val message: String = "",
        @SerializedName("Errors") @Expose val errors: List<Error> = emptyList())
