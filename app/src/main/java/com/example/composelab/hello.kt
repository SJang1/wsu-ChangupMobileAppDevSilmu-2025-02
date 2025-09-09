package com.example.composelab

fun main() {
    println("Hello World")

    fun sum(no: Int): Int {
        var sum = 0
        for (i in 1..no) {
            sum += 1
        }
        return sum
    }

    val name: String = "Sae Jin Kim"
    println("name: $name, sum: ${sum(10)}, plus: ${10 + 20}")
}