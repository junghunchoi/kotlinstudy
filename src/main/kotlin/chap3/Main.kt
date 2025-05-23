package com.emoney.chap3

// 코틀린에선 3항 연산자가 없기 때문에 아래와 같이 if문을 통해 변수 설정을 할 수 있다.
fun renamePackage(fullname: String, newName: String): String {
    val i = fullname.lastIndexOf(".")
    val prefix = if(i>=0) fullname.substring(0, i) else return newName
    return prefix
}

fun sumTest() {
    val a = IntArray(10) { it * it}
    var sum = 0

    for (i in a) {
        sum += i
    }
    println(sum)
    println(a[a.lastIndex])
}


fun main() {
//    println(renamePackage("foo.bar.old","new"))
    sumTest()
}