package com.example.composelab


val data1: Any = 10 // Any type
val data2: Nothing? = null // only null

fun main() {
    println("Hello World")

    fun sum(no: Int): Int {
        var sum = 0
        for (i in 1..no) {
            sum += i
        }
        return sum
    }

    var data = 10
    var result = if (data < 5) { // result = false
        print(" data < 5")
        true
    } else {
        print("data >= 5")
        false
    }

    val name: String = "Sae Jin Kim"
    println("name: $name, sum: ${sum(10)}, plus: ${10 + 20}")
}


fun 조건문() {

    var n = 8

    if (n < 1 || n > 9) {
        println("1부터 9까지의 숫자를 입력해주세요.")
    } else {
        when (n) {
            1 -> null//menuGuestInfo(userName, userMobile)
            2 -> null//menuCartItemList()
            3 -> null//menuCartClear()
            4 -> null//menuCartAddItem(mBook)
            5 -> null//menuCartRemoveItemCount()
            6 -> null//menuCartRemoveItem()
            7 -> null//menuCartBill()
            8 -> {
                null
                //menuExit()
                //quit = true
            }

            9 -> null//menuAdminLogin()
        }
    }
}